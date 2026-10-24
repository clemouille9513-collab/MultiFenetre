package com.exemple.multifenetre

import android.content.Context

/** Un raccourci : un nom, une disposition et une liste d'applis. */
data class Preset(val name: String, val layout: WindowLayout, val packages: List<String>)

/** Sauvegarde simple des raccourcis dans les SharedPreferences. */
object PresetStore {
    private const val PREFS = "presets"
    private const val KEY = "list"

    fun load(context: Context): List<Preset> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "") ?: ""
        return raw.split("\n").mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size < 3) return@mapNotNull null
            val layout = runCatching { WindowLayout.valueOf(parts[1]) }.getOrNull() ?: return@mapNotNull null
            Preset(parts[0], layout, parts[2].split(",").filter { it.isNotBlank() })
        }
    }

    fun save(context: Context, presets: List<Preset>) {
        val raw = presets.joinToString("\n") {
            "${it.name.replace("|", " ")}|${it.layout.name}|${it.packages.joinToString(",")}"
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, raw).apply()
    }
}
