package com.exemple.multifenetre

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper

data class AppInfo(val label: String, val packageName: String, val icon: Drawable)

object Launcher {

    /** Liste toutes les applis qui ont une icône de lancement, triées par nom. */
    fun installedApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName, it.loadIcon(pm)) }
            .filter { it.packageName != context.packageName }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    /**
     * Lance chaque appli dans sa propre fenêtre, placée selon [layout].
     * Fonctionne quand les fenêtres libres (freeform) sont activées sur le téléphone,
     * voir le README.
     */
    fun launch(
        context: Context,
        packages: List<String>,
        layout: WindowLayout,
        gapPx: Int
    ) {
        val dm = context.resources.displayMetrics
        val bounds = layout.compute(packages.size, dm.widthPixels, dm.heightPixels, gapPx)
        val handler = Handler(Looper.getMainLooper())

        packages.forEachIndexed { i, pkg ->
            val intent = context.packageManager.getLaunchIntentForPackage(pkg) ?: return@forEachIndexed
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
            )
            val options = ActivityOptions.makeBasic().setLaunchBounds(bounds[i])
            // Petit délai entre chaque lancement pour que le système place bien chaque fenêtre.
            handler.postDelayed({
                try {
                    context.startActivity(intent, options.toBundle())
                } catch (_: Exception) {
                    // Appli qui refuse d'être lancée : on passe à la suivante.
                }
            }, i * 600L)
        }
    }

    fun isInstalled(context: Context, pkg: String): Boolean = try {
        context.packageManager.getPackageInfo(pkg, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
