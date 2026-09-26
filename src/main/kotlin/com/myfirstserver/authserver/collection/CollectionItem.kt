package com.myfirstserver.authserver.collection

import com.myfirstserver.authserver.media.MediaType
import com.myfirstserver.authserver.users.User
import jakarta.persistence.*

/*
 * Represents the user's collection item with some important properties like title, artist, purchase infos,
 * and also the 'MediaType'
 */

@Entity
@Table(name = "CollectionItem")
class CollectionItem(

    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var artist: String,

    var releaseYear: Int? = null,

    var purchaseYear: Int? = null,

    var purchasePrice: Double? = null,

    @Column(length = 150)
    var notes: String? = null,

    // Many collections types can be assigned to a single type of MediaType
    @ManyToOne
    @JoinColumn(name = "mediaTypeId", nullable = false)
    var mediaType: MediaType,

    // Many collections can be assigned to a User
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    var user: User,
)