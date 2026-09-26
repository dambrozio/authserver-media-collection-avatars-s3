package com.myfirstserver.authserver.media.responses

import com.myfirstserver.authserver.media.MediaType

data class MediaTypeResponse(
    val id: Long?,
    val name: String,
    val description: String,
) {
    constructor(mediaType: MediaType) : this(
        id = mediaType.id,
        name = mediaType.name,
        description = mediaType.description,
    )
}