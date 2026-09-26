package com.myfirstserver.authserver.collection

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CollectionItemRepository :
    JpaRepository<CollectionItem, Long>,
    JpaSpecificationExecutor<CollectionItem> {

    fun findByUserId(userId: Long): List<CollectionItem>

    fun countByUserId(userId: Long): Long
}