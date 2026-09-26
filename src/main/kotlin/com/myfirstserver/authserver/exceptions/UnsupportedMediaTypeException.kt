package com.myfirstserver.authserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
class UnsupportedMediaTypeException(
    message: String = "Not found",
    cause: Throwable? = null
) : IllegalArgumentException(message, cause) {
    constructor (vararg types: String, cause: Throwable? = null) : this(
        "UnsupportedMediaTypeException:  Supported types: ${types.toList()}",
        cause
    )
}