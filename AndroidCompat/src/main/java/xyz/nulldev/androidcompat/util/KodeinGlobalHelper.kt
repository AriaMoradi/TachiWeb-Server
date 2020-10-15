package xyz.nulldev.androidcompat.util

import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

/**
 * Helper class to allow access to Kodein from Java
 */
class KodeinGlobalHelper {
    companion object {
        /**
         * Get the Kodein object
         */
        fun kodein() = DI.global

        /**
         * Get a dependency
         */
        @JvmStatic
        fun <T : Any> instance(type: Class<T>): T = kodein().instance<Any>(type) as T
        fun <T : Any> instance(type: Class<T>, kodein: DI? = null): T = (kodein ?: kodein()).instance<Any>(type) as T
    }
}
