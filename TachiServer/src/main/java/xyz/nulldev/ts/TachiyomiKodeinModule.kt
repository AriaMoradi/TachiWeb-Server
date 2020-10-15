package xyz.nulldev.ts

import android.content.Context
import com.google.gson.Gson
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.sync.LibrarySyncManager
import eu.kanade.tachiyomi.data.track.TrackManager
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.source.SourceManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.cache.AsyncDiskLFUCache
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import xyz.nulldev.ts.ext.kInstance
import xyz.nulldev.ts.ext.kInstanceLazy
import xyz.nulldev.ts.library.LibraryUpdater
import java.io.File

class TachiyomiKodeinModule {

    val context: Context by kInstance()

    fun create() = DI.Module("TachiyomiKodeinModule") {
        //Bridge to Tachiyomi dependencies
        bind<PreferencesHelper>() with singleton { Injekt.get() }

        bind<DatabaseHelper>() with singleton { Injekt.get() }

        bind<ChapterCache>() with singleton { Injekt.get() }

        bind<CoverCache>() with singleton { Injekt.get() }

        bind<NetworkHelper>() with singleton { Injekt.get() }

        bind<SourceManager>() with singleton { Injekt.get() }

        bind<ExtensionManager>() with singleton { Injekt.get() }

        bind<DownloadManager>() with singleton { Injekt.get() }

        bind<TrackManager>() with singleton { Injekt.get() }

        bind<LibrarySyncManager>() with singleton { Injekt.get() }

        bind<Gson>() with singleton { Injekt.get() }

        //Custom dependencies
        bind<BackupManager>() with singleton { BackupManager(this@TachiyomiKodeinModule.context) }

        bind<LibraryUpdater>() with singleton { LibraryUpdater() }

        bind<AsyncDiskLFUCache>() with singleton {
            AsyncDiskLFUCache(File(instance<ConfigManager>().module<ServerConfig>().rootDir, "alt-cover-cache"), 100000000 /* 100 MB */)
        }

        //Server dependencies
    }
}
