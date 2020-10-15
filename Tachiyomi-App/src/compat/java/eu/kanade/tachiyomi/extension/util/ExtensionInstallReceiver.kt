package eu.kanade.tachiyomi.extension.util

import android.content.Context
import eu.kanade.tachiyomi.extension.model.Extension
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import xyz.nulldev.androidcompat.pm.PackageController

/**
 * No-op listener
 */
internal class ExtensionInstallReceiver(private val listener: Listener) {
    private val controller by DI.global.instance<PackageController>()

    /**
     * Registers this broadcast receiver
     */
    fun register(context: Context) {
        controller.registerUninstallListener {
            listener.onPackageUninstalled(it)
        }
    }

    /**
     * Listener that receives extension installation events.
     */
    interface Listener {
        fun onExtensionInstalled(extension: Extension.Installed)
        fun onExtensionUpdated(extension: Extension.Installed)
        fun onExtensionUntrusted(extension: Extension.Untrusted)
        fun onPackageUninstalled(pkgName: String)
    }
}
