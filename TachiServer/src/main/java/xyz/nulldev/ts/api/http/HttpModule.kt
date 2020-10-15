package xyz.nulldev.ts.api.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.tika.mime.MimeTypes
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import xyz.nulldev.ts.api.http.serializer.FilterSerializer
import xyz.nulldev.ts.api.http.serializer.MangaSerializer
import xyz.nulldev.ts.api.task.TaskManager
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig

class HttpModule {
    fun create() = DI.Module("HttpModule") {
        bind<TaskManager>() with singleton { TaskManager() }

        bind<FilterSerializer>() with singleton { FilterSerializer() }

        bind<MangaSerializer>() with singleton { MangaSerializer() }

        bind<ObjectMapper>() with singleton {
            val serverConfig = this.instance<ConfigManager>().module<ServerConfig>()

            var mapper = ObjectMapper()
                    .registerKotlinModule()
                    .enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS)
            if (serverConfig.prettyPrintApi) mapper = mapper.enable(SerializationFeature.INDENT_OUTPUT)
            mapper
        }

        bind<MimeTypes>() with singleton { MimeTypes.getDefaultMimeTypes() }
    }
}
