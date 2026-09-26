package com.myfirstserver.authserver.media

import org.springframework.data.jpa.repository.JpaRepository

interface MediaTypeRepository : JpaRepository<MediaType, Long> {
    fun findByName(name: String): MediaType?
}