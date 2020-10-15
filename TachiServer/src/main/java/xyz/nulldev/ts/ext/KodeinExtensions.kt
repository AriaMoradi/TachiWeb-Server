package xyz.nulldev.ts.ext

import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

@Deprecated("All of kodein is lazy", replaceWith = ReplaceWith("kInstance()"))
inline fun <reified T : Any> kInstanceLazy() = DI.global.instance<T>()

inline fun <reified T : Any> kInstance() = DI.global.instance<T>()
