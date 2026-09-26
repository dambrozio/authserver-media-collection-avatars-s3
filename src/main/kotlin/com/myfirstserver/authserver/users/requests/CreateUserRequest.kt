package com.myfirstserver.authserver.users.requests

import com.myfirstserver.authserver.users.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreateUserRequest(
    @NotBlank
    var name: String?,
    @NotNull
    @Email
    var email: String?,
    @Pattern(regexp = $$"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    var password: String?
) {
    fun toUser() = User(
        name = name ?: "",
        email = email ?: "",
        password = password ?: ""
    )
}