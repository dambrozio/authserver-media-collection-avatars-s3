package com.myfirstserver.authserver.users.responses

import com.myfirstserver.authserver.users.User

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
) {
    constructor(user: User) : this(user.id!!, user.name, user.email)
}
