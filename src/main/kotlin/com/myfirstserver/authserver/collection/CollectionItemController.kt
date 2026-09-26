package com.myfirstserver.authserver.collection

import com.myfirstserver.authserver.collection.requests.CreateCollectionItemRequest
import com.myfirstserver.authserver.collection.responses.CollectionItemResponse
import com.myfirstserver.authserver.exceptions.UnauthorizedException
import com.myfirstserver.authserver.media.MediaTypeService
import com.myfirstserver.authserver.security.SecurityHelper
import com.myfirstserver.authserver.security.UserToken
import com.myfirstserver.authserver.users.User
import com.myfirstserver.authserver.users.UserService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/collection-items")
class CollectionItemController(
    val collectionItemService: CollectionItemService,
    val mediaTypeService: MediaTypeService,
    val securityHelper: SecurityHelper,
) {

    @SecurityRequirement(name = "jwt-auth")
    @PostMapping
    @ApiResponse(responseCode = "201")
    fun insert(
        authentication: Authentication?,
        @RequestBody @Valid request: CreateCollectionItemRequest
    ): ResponseEntity<CollectionItemResponse> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        val mediaType = mediaTypeService.findById(request.mediaTypeId)

        val collectionItem = CollectionItem(
            title = request.title,
            artist = request.artist,
            releaseYear = request.releaseYear,
            purchaseYear = request.purchaseYear,
            purchasePrice = request.purchasePrice,
            notes = request.notes,
            mediaType = mediaType,
            user = authenticatedUser
        )

        return collectionItemService.insert(collectionItem)
            .let { CollectionItemResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}")
    fun delete(
        authentication: Authentication?,
        @PathVariable id: Long
    ): ResponseEntity<Void> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        collectionItemService.canUserManageItem(
            id,
            authenticatedUser
        )

        collectionItemService.delete(id)

        return ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{id}")
    fun update(
        authentication: Authentication?,
        @PathVariable id: Long,
        @RequestBody @Valid request: CreateCollectionItemRequest
    ): ResponseEntity<CollectionItemResponse> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        collectionItemService.canUserManageItem(
            id,
            authenticatedUser
        )

        val mediaType = mediaTypeService.findById(request.mediaTypeId)

        val updatedItem = CollectionItem(
            title = request.title,
            artist = request.artist,
            releaseYear = request.releaseYear,
            purchaseYear = request.purchaseYear,
            purchasePrice = request.purchasePrice,
            notes = request.notes,
            mediaType = mediaType,
            user = authenticatedUser
        )

        return collectionItemService.update(id, updatedItem)
            .let { CollectionItemResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @GetMapping("/user/{userId}")
    fun findByUserId(
        authentication: Authentication?,
        @PathVariable userId: Long
    ): ResponseEntity<List<CollectionItemResponse>> {

        val authenticatedUser =
            securityHelper.getAuthenticatedUser(authentication)

        val isAdmin =
            authenticatedUser.roles.any {
                it.name.equals("ADMIN", ignoreCase = true)
            }

        val isOwner =
            authenticatedUser.id == userId

        if (!isAdmin && !isOwner) {

            throw UnauthorizedException(
                "You cannot access another user's collection."
            )
        }

        return collectionItemService.findByUserId(userId)
            .map { CollectionItemResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @GetMapping
    fun findAll(
        authentication: Authentication?
    ): ResponseEntity<List<CollectionItemResponse>> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        // Only ADMINs can get all user's collection items
        securityHelper.validateAdminAccess(authenticatedUser)

        return collectionItemService.findAll()
            .map { CollectionItemResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @GetMapping("/search")
    fun search(
        authentication: Authentication,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) artist: String?,
        @RequestParam(required = false) mediaType: String?,
        @RequestParam(defaultValue = "title") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String,
    ): ResponseEntity<List<CollectionItemResponse>> {

        val authenticatedUser = securityHelper.getAuthenticatedUser(authentication)

        return collectionItemService.search(
            authenticatedUser,
            title,
            artist,
            mediaType,
            sortBy,
            direction
        )
            .map { CollectionItemResponse(it) }
            .let { ResponseEntity.ok(it) }
    }
}