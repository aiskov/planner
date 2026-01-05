package com.aiskov.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject


lateinit var JSON: ObjectMapper

@ApplicationScoped()
class JsonProvider {
    @Inject
    private lateinit var mapper: ObjectMapper

    fun onStart(@Observes event: StartupEvent?) {
        JSON = mapper
    }
}