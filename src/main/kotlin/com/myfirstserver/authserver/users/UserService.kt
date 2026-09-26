package com.myfirstserver.authserver.users

import com.myfirstserver.authserver.exceptions.BadRequestException
import com.myfirstserver.authserver.exceptions.NotFoundException
import com.myfirstserver.authserver.exceptions.UnauthorizedException
import com.myfirstserver.authserver.roles.RoleRepository
import com.myfirstserver.authserver.security.Jwt
import com.myfirstserver.authserver.security.UserToken
import com.myfirstserver.authserver.users.responses.LoginResponse
import com.myfirstserver.authserver.users.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository,
    val avatarService: AvatarService,
    val jwt: Jwt
) {
    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw BadRequestException("User already exists")
        }
        return repository.save(user)
            .also { log.info("User {} added.", it.id) }
    }

    fun findAll(sortDir: SortDir): List<User> = when (sortDir) {
        SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("name").descending())
    }

    fun findByRole(roleName: String) = repository.findByRole(roleName.uppercase())

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun findById(id: Long) = findByIdOrNull(id) ?: throw NotFoundException(id)

    fun login(email: String, password: String): LoginResponse {
        val user = repository.findByEmail(email) ?: throw UnauthorizedException("User not found")
        if (user.password != password) {
            throw UnauthorizedException("Wrong password")
        }
        log.info("[AUTH-UserService] User '{}-{}' logged in.", user.id, user.name)
        return LoginResponse(
            token = jwt.createToken(user),
            user = UserResponse(user)
        )
    }

    // Manual upload of the avatar file

    fun saveAvatar(id: Long, avatar: MultipartFile): String {
        val user = findById(id)
        user.avatar = avatarService.save(user, avatar)
        repository.save(user)
        log.info("[AUTH-UserService] Avatar successfully saved to User: '{}'-'{}'", user.id, user.name)
        return avatarService.urlFor(user.avatar)
    }

    // Avatar download from required APIs (Gravatar, UI-Avatar)

    fun generateAvatar(id: Long): String {
        val user = findById(id)
    user.avatar = avatarService.regenerate(user)
        repository.save(user)
        log.info("[AUTH-UserService] Avatar successfully generated to User: '{}'-'{}'", user.id, user.name)
        return avatarService.urlFor(user.avatar)
    }


    fun getAvatarUrl(id: Long): String {
        val user = findById(id)
        return avatarService.urlFor(user.avatar)
    }

    fun validateAvatarModificationAccess(
        id: Long,
        authentication: Authentication
    ) {
        val token = authentication.principal as? UserToken
            ?: throw UnauthorizedException("User not authenticated")

        if (token.id != id && !token.isAdmin) {
            throw UnauthorizedException("Only admins can manage user's avatar")
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(UserService::class.java)
    }
}

