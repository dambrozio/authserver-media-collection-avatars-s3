package com.myfirstserver.authserver.security

import com.myfirstserver.authserver.exceptions.UnauthorizedException
import com.myfirstserver.authserver.users.User
import com.myfirstserver.authserver.users.UserService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class SecurityHelper(
    private val userService: UserService,
) {

    fun getAuthenticatedUser(
        authentication: Authentication?
    ): User {

        if (
            authentication == null ||
            authentication.principal == "anonymousUser"
        ) {
            throw UnauthorizedException(
                "User not authenticated"
            )
        }

        val userToken = authentication.principal as UserToken

        return userService.findById(userToken.id)
    }

    fun validateAdminAccess(
        user: User
    ) {

        val isAdmin =
            user.roles.any {
                it.name.equals("ADMIN", ignoreCase = true)
            }

        if (!isAdmin) {

            throw UnauthorizedException(
                "Only ADMIN users can manage this resource"
            )
        }
    }
}