package xyz.nulldev.ts.ext

import android.content.Context
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.RxSharedPreferences
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import eu.kanade.tachiyomi.data.preference.PreferenceKeys as Keys

private val prefs = PreferenceManager.getDefaultSharedPreferences({
    val context: Context by DI.global.instance()
    context
}())
private val rxPrefs = RxSharedPreferences.create(prefs)

val Keys.authPassword get() = "pref_ts_server_password"
fun PreferencesHelper.authPassword() = rxPrefs.getString(Keys.authPassword, "")
