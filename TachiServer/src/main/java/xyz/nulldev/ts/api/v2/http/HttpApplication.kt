package xyz.nulldev.ts.api.v2.http

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.core.compression.CompressionStrategy
import io.javalin.plugin.json.JavalinJackson
import xyz.nulldev.ts.api.http.auth.AuthController

class HttpApplication {
    val app = Javalin.create { config ->
        config.enableDevLogging()
        config.enableCorsForAllOrigins() // TODO Should we really enable CORs?
        config.compressionStrategy(CompressionStrategy.GZIP)
    }
        .start(4567)

    init {
        configureJackson()

        app.routes {
            path("auth") {
                post(AuthController::login)
                delete(AuthController::invalidateSession)
                post("clearall", AuthController::invalidateAll)
                get(AuthController::getSession)
            }
        }
    }

    fun configureJackson() {
        JavalinJackson.configure(jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        })
    }
}