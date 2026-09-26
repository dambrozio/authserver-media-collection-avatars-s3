package com.myfirstserver.authserver.roles.responses

import com.myfirstserver.authserver.roles.Role
import jakarta.validation.constraints.NotBlank

data class RoleResponse(
    @NotBlank
    val name: String,
    @NotBlank
    val description: String
) {
    constructor(role: Role) : this(role.name, role.description)
}