package uk.themeadow.doineedacache.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {
    override fun save(key: String, value: String) {
        context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE).edit().putString(key, value).apply()
    }

    override fun get(key: String): String? = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE).getString(key, null)

    private val PREFS_KEY = "myprefs"
}

interface SettingsRepository {
    fun save(key: String, value: String)
    fun get(key: String): String?
}