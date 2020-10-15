package xyz.nulldev.ts.api.v2.java.impl.extensions

import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.extension.model.Extension
import eu.kanade.tachiyomi.source.SourceManager
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import xyz.nulldev.androidcompat.pm.PackageController
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionsController
import xyz.nulldev.ts.ext.kInstance
import xyz.nulldev.ts.ext.kInstanceLazy
import java.io.File

class ExtensionsControllerImpl : ExtensionsController {
    private val controller by kInstance<PackageController>()

    override fun get(vararg packageNames: String)
            = ExtensionCollectionImpl(packageNames.toList()) // TODO Check these extensions exist

    override fun getAll()
        = ExtensionCollectionImpl(getAllExtensions().map { it.pkgName })

    override fun trust(hash: String) {
        manager.trustSignature(hash)
    }

    override fun reloadAvailable() {
        manager.findAvailableExtensions()
        manager.getAvailableExtensionsObservable().take(2).toBlocking().subscribe()
    }

    override fun reloadLocal() {
        manager.init({ val sourceManager: SourceManager by kInstance(); sourceManager }())
    }

    override fun installExternal(apk: File) {
        controller.installPackage(apk, true)
        reloadLocal()
    }

    companion object {
        private val manager by DI.global.instance<ExtensionManager>()

        internal fun getAllExtensions(): List<Extension> {
            var localExtensions = manager.installedExtensions +
                    manager.untrustedExtensions

            // Get available extensions excluding ones that have already been installed
            localExtensions += manager.availableExtensions.filter { avail ->
                localExtensions.none { it.pkgName == avail.pkgName }
            }

            return localExtensions
        }

        init {
            // Forcibly fill this initially to allow the reloadAvailable endpoint to function
            manager.findAvailableExtensions()
        }
    }
}