package com.myfirstserver.authserver.files

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.myfirstserver.authserver.users.User
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class S3Storage : FileStorage {
    private val s3: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.US_EAST_2)
        .withCredentials(EnvironmentVariableCredentialsProvider())
        .build()

    override fun save(
        user: User,
        path: String,
        content: InputStream,
        contentType: String,
        contentLength: Long,
        originalFilename: String
    ) {
        val meta = ObjectMetadata()
        meta.contentType = contentType
        meta.contentLength = contentLength
        meta.userMetadata["userId"] = "${user.id}"
        meta.userMetadata["originalFileName"] = originalFilename

        val transferManager = TransferManagerBuilder.standard()
            .withS3Client(s3)
            .build()
        transferManager
            .upload(
                PUBLIC,
                path,
                content,
                meta
            )
            .waitForUploadResult()
    }

    override fun load(path: String): Resource = InputStreamResource(
        s3.getObject(
            PUBLIC,
            path.replace("---", "/")
        ).objectContent
    )

    override fun urlFor(name: String): String = "$PREFIX/$name"

    companion object {
        const val PREFIX = "https://daoliv-authserver-public.s3.us-east-2.amazonaws.com"
        const val PUBLIC = "daoliv-authserver-public"
    }

}