package com.aiskov.config

import com.aiskov.utils.json.SecretMaskingModule
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject


lateinit var JSON: ObjectMapper

@ApplicationScoped()
class JsonConfig {
    @Inject
    private lateinit var mapper: ObjectMapper

    fun onStart(@Observes event: StartupEvent?) {
        mapper.registerModule(SecretMaskingModule())
        JSON = mapper
    }
}