package com.myfirstserver.authserver.files

import com.myfirstserver.authserver.exceptions.NotFoundException
import com.myfirstserver.authserver.users.User
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import java.io.InputStream
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.isRegularFile

class FileSystemStorage : FileStorage {

    override fun save(
        user: User,
        path: String,
        content: InputStream,
        contentType: String,
        contentLength: Long,
        originalFilename: String
    ) {
        val root = Paths.get(ROOT)
        val destinationFile = root.resolve(path).normalize().toAbsolutePath()
        Files.createDirectories(destinationFile.parent)
        content.use {
            Files.copy(
                it, destinationFile, StandardCopyOption.REPLACE_EXISTING
            )
        }
    }

    override fun load(path: String): Resource =
        Paths.get(ROOT, path.replace("---", "/")).takeIf { it.isRegularFile() }?.let { UrlResource(it.toUri()) }
            ?: throw NotFoundException("File not found: $path")


    override fun urlFor(name: String): String =
        "http://localhost:8080/api/files" +
                URLEncoder.encode(
                    name.replace("/", "---"), "UTF-8"
                )

    companion object {
        const val ROOT = "./fs"
    }
}