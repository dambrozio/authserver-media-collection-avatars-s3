package com.myfirstserver.authserver.media

import jakarta.persistence.*

// Represents the media type, that can be like Vinyl, CD, VHS

@Entity
@Table(name = "MediaType")
class MediaType(

    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var description: String,
)