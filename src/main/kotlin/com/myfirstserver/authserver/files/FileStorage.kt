package com.myfirstserver.authserver.files

import com.myfirstserver.authserver.users.User
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface FileStorage {

    /*
     * Manual avatar upload: MultipartFile
     * Automatic avatar fetch: InputStream
     */
    fun save(user: User, path: String, file: MultipartFile) = save(
        user = user,
        path = path,
        content = file.inputStream,
        contentType = file.contentType ?: "application/octet-stream",
        contentLength = file.size,
        originalFilename = file.originalFilename ?: "upload"
    )

    fun save(
        user: User,
        path: String,
        content: InputStream,
        contentType: String,
        contentLength: Long,
        originalFilename: String
    )

    fun load(path: String): Resource
    fun urlFor(name: String): String
}