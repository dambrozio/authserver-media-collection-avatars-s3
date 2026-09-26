package com.myfirstserver.authserver.media.requests

data class CreateMediaTypeRequest(
    val name: String,
    val description: String,
)