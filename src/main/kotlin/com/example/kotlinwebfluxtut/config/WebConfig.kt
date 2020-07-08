package com.example.kotlinwebfluxtut.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
@ComponentScan("com.example.kotlinwebfluxtut")
class WebConfig: WebFluxConfigurer {
    // I think this is to allow us to make a request to
    // jsonplaceholder.typi.com within our code, which
    // lives on a separate server.
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("api/**")
    }
}