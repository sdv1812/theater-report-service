package com.sanskar.theaterreport

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/healthcheck")
    fun healthcheck() : ResponseEntity<String> {
        return ResponseEntity.ok().body("Ok")
    }
}