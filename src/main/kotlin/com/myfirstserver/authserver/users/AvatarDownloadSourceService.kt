package com.myfirstserver.authserver.users

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

data class DownloadedAvatar(
    val bytes: ByteArray,
    val contentType: String,
    val originalFilename: String
)

@Service

class AvatarDownloadSourceService {
    private val client = RestClient.create()

    /**
     * Download the avatar for the user.
     *
     * Firstly, check if is there an avatar from "Gravatar",
     * If not, download from initials avatar from "Ui-Avatars"
     */
    fun downloadForUser(user: User): DownloadedAvatar =
        downloadFromGravatar(user.email) ?: downloadInitialsAvatar(user.name)

    private fun downloadFromGravatar(email: String): DownloadedAvatar? =
        download(
            url = "$GRAVATAR_URL/${sha256(email.trim().lowercase())}?d=404&s=$AVATAR_SIZE",
            originalFilename = "gravatar.png"
        ).also { avatar ->
            if (avatar == null) {
                log.info(
                    "[AUTH-AvatarDownloadSourceService] downloadFromGravatar: No 'Gravatar' was found for the email: {}",
                    email
                )
            }
        }

    private fun downloadInitialsAvatar(name: String): DownloadedAvatar {
        log.info(
            "[AUTH-AvatarDownloadSourceService] downloadInitialsAvatar: As a fallback -> Download from Initials 'UI-Avatar'"
        )
        return download(
            url = UriComponentsBuilder.fromUriString(UI_AVATARS_URL)
                .queryParam("name", name.ifBlank { "User" })
                .queryParam("format", "png")
                .queryParam("size", AVATAR_SIZE)
                .queryParam("background", "random")
                .build()
                .encode()
                .toUriString(),
            originalFilename = "initials.png"
        ) ?: throw IllegalStateException("Could not generate an initials avatar")
    }

    private fun download(url: String, originalFilename: String): DownloadedAvatar? =
        try {
            client.get()
                .uri(url)
                .exchange { _, response ->
                    if (!response.statusCode.is2xxSuccessful) return@exchange null

                    val contentType = response.headers.contentType
                        ?.takeIf { it.type == "image" }
                        ?: return@exchange null
                    val bytes = response.bodyTo(ByteArray::class.java)
                        ?.takeIf { it.isNotEmpty() }
                        ?: return@exchange null

                    DownloadedAvatar(
                        bytes = bytes,
                        contentType = contentType.toString(),
                        originalFilename = originalFilename
                    )
                }
        } catch (exception: Exception) {
            log.warn("Could not download avatar from {}: {}", url.substringBefore("?"), exception.message)
            null
        }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }

    companion object {
        // https://gravatar.com/avatar/HASH
        private const val GRAVATAR_URL = "https://www.gravatar.com/avatar"

        // https://ui-avatars.com/api/?name=John+Doe
        private const val UI_AVATARS_URL = "https://ui-avatars.com/api/"

        private const val AVATAR_SIZE: Int = 256

        private val log = LoggerFactory.getLogger(AvatarDownloadSourceService::class.java)
    }
}