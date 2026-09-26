package com.myfirstserver.authserver.users

import com.myfirstserver.authserver.collection.CollectionItem
import com.myfirstserver.authserver.roles.Role
import jakarta.persistence.*

@Entity
@Table(name = "UserTable")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    var password: String = "",
    var name: String = "",

    @ManyToMany
    @JoinTable(
        name = "UserRole",
        joinColumns = [JoinColumn(name = "idUser")],
        inverseJoinColumns = [JoinColumn(name = "idRole")]
    )
    var roles: MutableSet<Role> = mutableSetOf(),
    var avatar: String = AvatarService.DEFAULT_AVATAR,

    // The user can own many collection items
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var collectionItems: MutableList<CollectionItem> = mutableListOf(),

    ) {
    @Transient
    fun isAdmin() = roles.any { r -> r.name == "ADMIN" }
}