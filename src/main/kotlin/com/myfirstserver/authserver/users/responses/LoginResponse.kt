package com.myfirstserver.authserver.users.responses

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
