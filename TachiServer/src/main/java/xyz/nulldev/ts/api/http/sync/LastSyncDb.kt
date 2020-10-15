package xyz.nulldev.ts.api.http.sync

import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.conf.global
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import xyz.nulldev.ts.ext.kInstance
import java.io.File

class LastSyncDb(override val di: DI = DI.global): DIAware {
    private val serverConfig by lazy {
        val configManager by kInstance<ConfigManager>()
        configManager.module<ServerConfig>()
    }
    private val dbFile = File(serverConfig.rootDir, "last_sync.db")

    val lastSyncs by lazy {
        if(dbFile.exists()) {
            dbFile.readLines().associate {
                Pair(it.substringAfter(':'),
                        it.substringBefore(':').toLong())
            }.toMutableMap()
        } else {
            //No existing DB, use empty map
            mutableMapOf()
        }
    }

    @Synchronized
    fun save() {
        val text = lastSyncs.map {
            "${it.value}:${it.key}"
        }.joinToString(separator = "\n")

        dbFile.writeText(text)
    }
}