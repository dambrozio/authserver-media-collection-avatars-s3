package com.myfirstserver.authserver

import com.myfirstserver.authserver.collection.CollectionItem
import com.myfirstserver.authserver.collection.CollectionItemRepository
import com.myfirstserver.authserver.media.MediaType
import com.myfirstserver.authserver.media.MediaTypeRepository
import com.myfirstserver.authserver.roles.Role
import com.myfirstserver.authserver.roles.RoleRepository
import com.myfirstserver.authserver.users.User
import com.myfirstserver.authserver.users.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val mediaTypeRepository: MediaTypeRepository,
    val collectionItemRepository: CollectionItemRepository,
) : ApplicationListener<ContextRefreshedEvent> {

    private lateinit var cassette: MediaType
    private lateinit var cd: MediaType
    private lateinit var vinyl: MediaType

    private lateinit var adminRole: Role
    private lateinit var premiumRole: Role
    private lateinit var freeRole: Role

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        setMediaTypes()
        setAccountRoles()
        generateAccounts()
    }

    private fun setMediaTypes() {
        vinyl =
            mediaTypeRepository.findByName("VINYL")
                ?: mediaTypeRepository.save(
                    MediaType(
                        name = "VINYL",
                        description = "Vinyl Record"
                    )
                )

        cd =
            mediaTypeRepository.findByName("CD")
                ?: mediaTypeRepository.save(
                    MediaType(
                        name = "CD",
                        description = "Compact Disc"
                    )
                )

        cassette =
            mediaTypeRepository.findByName("CASSETTE")
                ?: mediaTypeRepository.save(
                    MediaType(
                        name = "CASSETTE",
                        description = "Cassette Tape"
                    )
                )
    }

    private fun setAccountRoles() {
        adminRole =
            roleRepository.findByName("ADMIN")
                ?: roleRepository.save(
                    Role(
                        name = "ADMIN",
                        description = "System Administrator"
                    )
                )

        premiumRole =
            roleRepository.findByName("PREMIUM")
                ?: roleRepository.save(
                    Role(
                        name = "PREMIUM",
                        description = "Premium User"
                    )
                )

        freeRole = roleRepository.findByName("FREE")
            ?: roleRepository.save(
                Role(
                    name = "FREE",
                    description = "Free User"
                )
            )
    }

    private fun generateAccounts() {
        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                email = "admin@authserver.com",
                password = "admin",
                name = "Auth Server Administrator"
            ).also {
                it.roles.add(adminRole)
            }
            userRepository.save(admin)
        }

        if (userRepository.findByRole("PREMIUM").isEmpty()) {
            val userPremium = User(
                email = "daniel.ambrozio.oliveira@gmail.com",
                password = "daniel",
                name = "Daniel (Premium Unlimited)"
            ).also {
                it.roles.add(premiumRole)
            }
            userRepository.save(userPremium)
            setCollectionItemsToUserPremium(userPremium)
        }


        val userPremium2 = User(
            email = "nawatavares@hotmail.com",
            password = "nawa",
            name = "Nawa (Premium Unlimited)"
        ).also {
            it.roles.add(premiumRole)
        }
        userRepository.save(userPremium2)
        setCollectionItemsToUserPremium(userPremium2)


        val userPremium3 = User(
            email = "renatamonteclaro@gmail.com",
            password = "renata",
            name = "Renata (Premium Unlimited)"
        ).also {
            it.roles.add(premiumRole)
        }
        userRepository.save(userPremium3)
        setCollectionItemsToUserPremium(userPremium3)

        if (userRepository.findByRole("FREE").isEmpty()) {
            val userFree = User(
                email = "lineu.ambrozio@google.com",
                password = "lineu",
                name = "Lineu (Free trial)"
            ).also {
                it.roles.add(freeRole)
            }
            userRepository.save(userFree)
            setCollectionItemsToUserFree(userFree)
        }

        val userFree2 = User(
            email = "joaovgama93@gmail.com",
            password = "joao",
            name = "Joao (Free trial)"
        ).also {
            it.roles.add(freeRole)
        }
        userRepository.save(userFree2)
        setCollectionItemsToUserFree(userFree2)


        val userFree3 = User(
            email = "daniel_ambrozio@hotmail.com",
            password = "daniel",
            name = "Daniel (Free trial)"
        ).also {
            it.roles.add(freeRole)
        }
        userRepository.save(userFree3)
        setCollectionItemsToUserFree(userFree3)
    }


    private fun setCollectionItemsToUserFree(userFree: User) {
        collectionItemRepository.save(
            CollectionItem(
                title = "Oops!... I Did It Again",
                artist = "Britney Spears",
                releaseYear = 2000,
                purchaseYear = 2025,
                purchasePrice = 89.90,
                notes = "Special Pop Collection",
                mediaType = cd,
                user = userFree
            )
        )

        collectionItemRepository.save(
            CollectionItem(
                title = "Master of Puppets",
                artist = "Metallica",
                releaseYear = 1986,
                purchaseYear = 2026,
                purchasePrice = 219.90,
                notes = "Remastered Collector CD",
                mediaType = cd,
                user = userFree
            )
        )

        collectionItemRepository.save(
            CollectionItem(
                title = "No Strings Attached",
                artist = "NSYNC",
                releaseYear = 2000,
                purchaseYear = 2022,
                purchasePrice = 69.90,
                notes = "Vintage Cassette",
                mediaType = cassette,
                user = userFree
            )
        )
    }

    private fun setCollectionItemsToUserPremium(userPremium: User) {

        collectionItemRepository.save(
            CollectionItem(
                title = "Hybrid Theory",
                artist = "Linkin Park",
                releaseYear = 2000,
                purchaseYear = 2026,
                purchasePrice = 149.90,
                notes = "Limited Edition Vinyl",
                mediaType = vinyl,
                user = userPremium
            )
        )
        CollectionItem(
            title = "Once",
            artist = "Nightwish",
            releaseYear = 2004,
            purchaseYear = 2025,
            purchasePrice = 199.90,
            notes = "Symphonic Metal Vinyl Edition",
            mediaType = vinyl,
            user = userPremium
        )


        collectionItemRepository.save(
            CollectionItem(
                title = "Meteora",
                artist = "Linkin Park",
                releaseYear = 2003,
                purchaseYear = 2025,
                purchasePrice = 119.90,
                notes = "Imported CD",
                mediaType = cd,
                user = userPremium
            )
        )

        collectionItemRepository.save(
            CollectionItem(
                title = "Californication",
                artist = "Red Hot Chili Peppers",
                releaseYear = 1999,
                purchaseYear = 2025,
                purchasePrice = 189.90,
                notes = "Remastered Vinyl",
                mediaType = vinyl,
                user = userPremium
            )
        )

        collectionItemRepository.save(
            CollectionItem(
                title = "Fallen",
                artist = "Evanescence",
                releaseYear = 2003,
                purchaseYear = 2024,
                purchasePrice = 99.90,
                notes = "Original Press",
                mediaType = cd,
                user = userPremium
            )
        )

        collectionItemRepository.save(
            CollectionItem(
                title = "From Zero",
                artist = "Linkin Park",
                releaseYear = 2024,
                purchaseYear = 2026,
                purchasePrice = 249.90,
                notes = "Latest studio album featuring Emily Armstrong",
                mediaType = vinyl,
                user = userPremium
            )
        )

        collectionItemRepository.save(
            CollectionItem(
                title = "MAYHEM",
                artist = "Lady Gaga",
                releaseYear = 2025,
                purchaseYear = 2026,
                purchasePrice = 249.90,
                notes = "Target Limited Press",
                mediaType = vinyl,
                user = userPremium
            )
        )

    }

}
