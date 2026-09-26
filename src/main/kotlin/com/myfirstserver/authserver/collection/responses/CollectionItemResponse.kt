package com.myfirstserver.authserver.collection.responses

import com.myfirstserver.authserver.collection.CollectionItem

data class CollectionItemResponse(
    val id: Long?,
    val title: String,
    val artist: String,
    val mediaType: String,
    val userId: Long?,
) {
    constructor(item: CollectionItem) : this(
        id = item.id,
        title = item.title,
        artist = item.artist,
        mediaType = item.mediaType.name,
        userId = item.user.id,
    )
}