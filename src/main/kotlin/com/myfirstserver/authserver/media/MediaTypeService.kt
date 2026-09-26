package com.myfirstserver.authserver.media

import com.myfirstserver.authserver.exceptions.BadRequestException
import com.myfirstserver.authserver.exceptions.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MediaTypeService(
    private val repository: MediaTypeRepository,
) {

    fun insert(mediaType: MediaType): MediaType {

        if (repository.findByName(mediaType.name.uppercase()) != null) {

            log.error("[AUTH-MediaTypeService] MediaType {} already exists.", mediaType.name)

            throw BadRequestException(
                "MediaType ${mediaType.name} already exists."
            )
        }

        mediaType.name = mediaType.name.uppercase()

        return repository.save(mediaType)
            .also {
                log.info("[AUTH-MediaTypeService] MediaType {} added.", it.id)
            }
    }

    fun update(
        id: Long,
        updatedMediaType: MediaType,
    ): MediaType {

        val mediaType = findById(id)

        mediaType.name = updatedMediaType.name.uppercase()
        mediaType.description = updatedMediaType.description

        return repository.save(mediaType)
            .also {
                log.info("[AUTH-MediaTypeService] MediaType {} updated.", it.id)
            }
    }

    fun delete(id: Long) {

        val mediaType = findById(id)

        repository.delete(mediaType)

        log.warn("[AUTH-MediaTypeService] MediaType {} deleted.", id)
    }

    fun findById(id: Long): MediaType {

        return repository.findById(id)
            .orElseThrow {

                log.error("[AUTH-MediaTypeService] MediaType {} not found.", id)

                NotFoundException("MediaType $id not found.")
            }
    }

    fun findAll(): List<MediaType> {

        return repository.findAll()
            .also {
                log.info("Listing all MediaTypes.")
            }
    }

    companion object {
        val log = LoggerFactory.getLogger(MediaTypeService::class.java)
    }
}