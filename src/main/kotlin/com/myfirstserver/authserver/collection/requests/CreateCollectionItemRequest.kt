package com.myfirstserver.authserver.collection.requests

data class CreateCollectionItemRequest(
    val title: String,
    val artist: String,
    val releaseYear: Int?,
    val purchaseYear: Int?,
    val purchasePrice: Double?,
    val notes: String?,
    val mediaTypeId: Long,
)