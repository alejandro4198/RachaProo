package com.example.rachapro.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class RachaproBackendApplication

fun main(args: Array<String>) {
    runApplication<RachaproBackendApplication>(*args)
}