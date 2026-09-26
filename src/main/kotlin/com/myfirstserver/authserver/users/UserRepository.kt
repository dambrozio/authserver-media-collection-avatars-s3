package com.myfirstserver.authserver.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    @Query(
        """select distinct u from User u
        join u.roles r
        where r.name = :role
        order by u.name""",
    )
    fun findByRole(role: String): List<User>
}

/*
@Component
class UserRepository(private val openAPIService: OpenAPIService) {
    private val users = mutableMapOf<Long, User>()

    fun save(user: User): User {
        if (user.id == null) {
            user.id = (users.keys.maxOrNull() ?: 0) + 1
        }
        users[user.id!!] = user
        return user
    }

    fun findAll(): List<User> = users.values.toList()

    fun findAll(sortDir: SortDir): List<User> =
        if (sortDir == SortDir.ASC) users.values.sortedBy { it.name }
        else users.values.sortedByDescending { it.name }

    fun findByIdOrNull(id: Long): User? = users.values.find { it.id == id }

    fun deleleById(id: Long) = users.remove(id) != null
}*/