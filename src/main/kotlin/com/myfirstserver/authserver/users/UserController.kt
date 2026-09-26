package com.myfirstserver.authserver.users

import com.myfirstserver.authserver.exceptions.UnauthorizedException
import com.myfirstserver.authserver.security.UserToken
import com.myfirstserver.authserver.users.requests.LoginRequest
import com.myfirstserver.authserver.users.responses.UserResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid user: LoginRequest
    ) = userService.login(user.email!!, user.password!!)

    @GetMapping
    fun list(
        @RequestParam sortDir: String?,
        @RequestParam role: String?
    ): ResponseEntity<List<UserResponse>> {
        val users = if (role != null) userService.findByRole(role)
        else userService.findAll(SortDir.find(sortDir ?: "ASC"))
        return users
            .map { UserResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{id}/avatar", consumes = ["multipart/form-data"])
    fun uploadAvatar(
        @PathVariable id: Long,
        @RequestParam avatar: MultipartFile,
        authentication: Authentication
    ): ResponseEntity<Void> {
        userService.validateAvatarModificationAccess(id, authentication)

        return userService.saveAvatar(id, avatar)
            .let { ResponseEntity.created(URI(it)).build<Void>() }
    }

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}/avatar")
    fun regenerateAvatar(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        userService.validateAvatarModificationAccess(id, authentication)

    return userService.generateAvatar(id)
            .let { ResponseEntity.created(URI(it)).build<Void>() }
    }

    @GetMapping("/{id}/avatar")
    fun getAvatar(
        @PathVariable id: Long
    ): ResponseEntity<Map<String, String>> =
        ResponseEntity.ok(
            mapOf("avatar" to userService.getAvatarUrl(id))
        )

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @GetMapping("/me/avatar")
    fun getMyAvatar(
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val token = authentication.principal as? UserToken
            ?: throw UnauthorizedException("User not authenticated")

        return ResponseEntity.ok(
            mapOf("avatar" to userService.getAvatarUrl(token.id))
        )
    }
}