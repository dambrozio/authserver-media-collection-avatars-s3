package com.myfirstserver.authserver.collection

import com.myfirstserver.authserver.exceptions.ForbiddenException
import com.myfirstserver.authserver.exceptions.NotFoundException
import com.myfirstserver.authserver.exceptions.UnauthorizedException
import com.myfirstserver.authserver.users.User
import jakarta.persistence.criteria.Predicate
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class CollectionItemService(
    private val repository: CollectionItemRepository,
) {

    fun insert(collectionItem: CollectionItem): CollectionItem {

        // Validate the items count
        validateCollectionLimit(collectionItem.user)

        return repository.save(collectionItem)
            .also {
                log.info("[AUTH-CollectionItemService] CollectionItem {} added.", it.id)
            }
    }

    fun update(
        id: Long,
        updatedCollectionItem: CollectionItem,
    ): CollectionItem {

        val collectionItem = findById(id)

        collectionItem.title = updatedCollectionItem.title
        collectionItem.artist = updatedCollectionItem.artist
        collectionItem.releaseYear = updatedCollectionItem.releaseYear
        collectionItem.purchaseYear = updatedCollectionItem.purchaseYear
        collectionItem.purchasePrice = updatedCollectionItem.purchasePrice
        collectionItem.notes = updatedCollectionItem.notes
        collectionItem.mediaType = updatedCollectionItem.mediaType

        return repository.save(collectionItem)
            .also {
                log.info("[AUTH-CollectionItemService] CollectionItem {} updated.", it.id)
            }
    }

    fun delete(id: Long) {
        val collectionItem = findById(id)
        repository.delete(collectionItem)
        log.warn("[AUTH-CollectionItemService] CollectionItem {} deleted.", id)
    }

    fun findById(id: Long): CollectionItem {

        return repository.findById(id)
            .orElseThrow {
                log.error("[AUTH-CollectionItemService] CollectionItem {} not found.", id)
                NotFoundException("CollectionItem $id not found.")
            }
    }

    fun findByUserId(userId: Long): List<CollectionItem> {

        log.info("[AUTH-CollectionItemService] Listing CollectionItems from user {}.", userId)

        return repository.findByUserId(userId)
    }

    private fun validateCollectionLimit(user: User) {
        log.info(
            "[AUTH-CollectionItemService] Check limit for user {} ",
            user.id
        )
        val isPremium =
            user.roles.any {
                it.name.equals("PREMIUM", ignoreCase = true)
            }

        if (isPremium) {
            return
        }

        val totalItems = repository.countByUserId(user.id!!)

        if (totalItems >= FREE_USER_ITEMS_LIMIT) {

            log.warn(
                "[AUTH-CollectionItemService] User {} has reached the FREE limit",
                user.id
            )

            throw ForbiddenException(
                "FREE users can only have up to 5 collection items"
            )
        }
    }

    fun canUserManageItem(
        collectionItemId: Long,
        authenticatedUser: User
    ) {

        val collectionItem = findById(collectionItemId)

        val isAdmin =
            authenticatedUser.roles.any {
                it.name.equals("ADMIN", ignoreCase = true)
            }

        if (isAdmin) {
            return
        }

        if (collectionItem.user.id != authenticatedUser.id) {
            throw UnauthorizedException(
                "You cannot modify another user's collection item."
            )
        }
    }

    fun search(
        user: User,
        title: String?,
        artist: String?,
        mediaType: String?,
        sortBy: String,
        direction: String
    ): List<CollectionItem> {

        val specification = Specification<CollectionItem> { root, _, criteriaBuilder ->

            val predicates = mutableListOf<Predicate>()

            predicates.add(
                criteriaBuilder.equal(
                    root.get<Any>("user").get<Long>("id"),
                    user.id
                )
            )

            title?.let {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%${it.lowercase()}%"
                    )
                )
            }

            artist?.let {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("artist")),
                        "%${it.lowercase()}%"
                    )
                )
            }

            mediaType?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        criteriaBuilder.lower(
                            root.get<Any>("mediaType").get<String>("name")
                        ),
                        it.lowercase()
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }

        val sort = Sort.by(
            Sort.Direction.fromString(direction),
            sortBy
        )

        log.info("[AUTH-CollectionItemService] Searching CollectionItems.")

        return repository.findAll(specification, sort)
    }

    fun findAll(): List<CollectionItem> {
        return repository.findAll()
            .also {
                log.info("[AUTH-CollectionItemService] Listing all CollectionItems.")
            }
    }

    companion object {
        val log = LoggerFactory.getLogger(CollectionItemService::class.java)
        const val FREE_USER_ITEMS_LIMIT = 5
    }
}