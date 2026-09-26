package com.myfirstserver.authserver.media

import com.myfirstserver.authserver.exceptions.UnauthorizedException
import com.myfirstserver.authserver.media.requests.CreateMediaTypeRequest
import com.myfirstserver.authserver.media.responses.MediaTypeResponse
import com.myfirstserver.authserver.security.SecurityHelper
import com.myfirstserver.authserver.security.UserToken
import com.myfirstserver.authserver.users.User
import com.myfirstserver.authserver.users.UserService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springdoc.core.service.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/media-types")
class MediaTypeController(
    val mediaTypeService: MediaTypeService,
    val securityHelper: SecurityHelper,
) {

    @SecurityRequirement(name = "jwt-auth")
    @PostMapping
    @ApiResponse(responseCode = "201")
    fun insert(
        authentication: Authentication?,
        @RequestBody @Valid request: CreateMediaTypeRequest
    ): ResponseEntity<MediaTypeResponse> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        // Only ADMINs can add a new MediaType
        securityHelper.validateAdminAccess(authenticatedUser)

        val mediaType = MediaType(
            name = request.name,
            description = request.description
        )

        return mediaTypeService.insert(mediaType)
            .let { MediaTypeResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}")
    fun delete(
        authentication: Authentication?,
        @PathVariable id: Long
    ): ResponseEntity<Void> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        // Only ADMINs can remove a MediaType
        securityHelper.validateAdminAccess(authenticatedUser)

        mediaTypeService.delete(id)

        return ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{id}")
    fun update(
        authentication: Authentication?,
        @PathVariable id: Long,
        @RequestBody @Valid request: CreateMediaTypeRequest
    ): ResponseEntity<MediaTypeResponse> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        // Only ADMINs can update a MediaType
        securityHelper.validateAdminAccess(authenticatedUser)

        val mediaType = MediaType(
            name = request.name,
            description = request.description
        )

        return mediaTypeService.update(id, mediaType)
            .let { MediaTypeResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ) = MediaTypeResponse(mediaTypeService.findById(id))
        .let { ResponseEntity.ok(it) }

    @GetMapping
    fun findAll() =
        mediaTypeService.findAll()
            .map { MediaTypeResponse(it) }
            .let { ResponseEntity.ok(it) }
}