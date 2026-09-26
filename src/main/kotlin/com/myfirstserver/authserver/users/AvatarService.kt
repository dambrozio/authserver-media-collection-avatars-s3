package com.myfirstserver.authserver.users

import com.myfirstserver.authserver.exceptions.UnsupportedMediaTypeException
import com.myfirstserver.authserver.files.FileStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream

@Service
class AvatarService(
    @Qualifier("fileStorage") private val storage: FileStorage,
    private val source: AvatarDownloadSourceService,
) {

    fun save(user: User, avatar: MultipartFile): String {
        try {
            val path = avatarPath(user, avatar.contentType)
            storage.save(user, "$ROOT/$path", avatar)
            return path
        } catch (exception: Exception) {
            log.warn(
                "Could not save user ${user.id} " +
                        "avatar ${avatar.originalFilename}: to storage: ${exception.message}"
            )
            return DEFAULT_AVATAR
        }
    }

    private fun avatarPath(user: User, contentType: String?): String {
        val extension = when (contentType?.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            else -> throw UnsupportedMediaTypeException("jpg", "png")
        }
        return "${user.id}/a_${user.id}.$extension"
    }


    /**
     * Downloads the available avatar (Gravatar and Ui-avatar) and makes the S3 copy
     */
    fun regenerate(user: User): String {
        try {
            val avatar = source.downloadForUser(user)
            val path = avatarPath(user, avatar.contentType)
            ByteArrayInputStream(avatar.bytes).use { content ->
                storage.save(
                    user = user,
                    path = "$ROOT/$path",
                    content = content,
                    contentType = avatar.contentType,
                    contentLength = avatar.bytes.size.toLong(),
                    originalFilename = avatar.originalFilename
                )
            }
            return path
        } catch (exception: Exception) {
            log.warn("Could not generate an avatar for user {}: {}", user.id, exception.message)
            return DEFAULT_AVATAR
        }
    }

    fun urlFor(path: String) = storage.urlFor("$ROOT/$path")

    companion object {
        const val ROOT = "avatares"
        const val DEFAULT_AVATAR = "default.png"
        private val log = LoggerFactory.getLogger(AvatarService::class.java)

    }
}