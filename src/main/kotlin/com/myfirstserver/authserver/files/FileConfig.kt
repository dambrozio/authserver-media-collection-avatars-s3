package com.myfirstserver.authserver.files

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class FileConfig {

    @Profile("!fs")
    @Bean("fileStorage")
    fun s3Storage() = S3Storage()

    @Profile("fs")
    @Bean("fileStorage")
    fun localStorage() = FileSystemStorage()
}