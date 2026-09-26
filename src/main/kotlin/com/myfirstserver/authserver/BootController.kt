package com.myfirstserver.authserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BootController {
    @GetMapping("/health")
    fun healthCheck() = mapOf("status" to "OK")
}