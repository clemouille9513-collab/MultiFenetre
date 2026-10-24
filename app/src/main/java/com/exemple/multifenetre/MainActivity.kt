package com.exemple.multifenetre

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(Modifier.fillMaxSize()) { MultiFenetreScreen() }
            }
        }
    }
}

private fun Drawable.toBitmap(size: Int = 96): Bitmap {
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    setBounds(0, 0, size, size)
    draw(canvas)
    return bmp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiFenetreScreen() {
    val context = LocalContext.current
    val apps = remember { Launcher.installedApps(context) }
    val selected = remember { mutableStateListOf<String>() } // l'ordre de sélection = ordre des fenêtres
    var layout by remember { mutableStateOf(WindowLayout.GRID) }
    var gapDp by remember { mutableStateOf(4f) }
    var search by remember { mutableStateOf("") }
    var presets by remember { mutableStateOf(PresetStore.load(context)) }
    var showSave by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Multi Fenêtre+", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Choisis 2 applis ou plus, une disposition, puis lance.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(12.dp))

        // --- Raccourcis enregistrés ---
        if (presets.isNotEmpty()) {
            Text("Raccourcis", style = MaterialTheme.typography.titleSmall)
            LazyColumn(Modifier.heightIn(max = 120.dp)) {
                items(presets) { p ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            Launcher.launch(context, p.packages.filter { Launcher.isInstalled(context, it) }, p.layout, (gapDp * context.resources.displayMetrics.density).toInt())
                        }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("▶  ${p.name}  (${p.packages.size} applis, ${p.layout.label})", Modifier.weight(1f))
                        TextButton(onClick = {
                            presets = presets - p
                            PresetStore.save(context, presets)
                        }) { Text("Suppr.") }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // --- Disposition ---
        Text("Disposition", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            WindowLayout.values().forEach { l ->
                FilterChip(
                    selected = layout == l,
                    onClick = { layout = l },
                    label = { Text(l.label) }
                )
            }
        }

        // --- Espace entre fenêtres ---
        Text("Espace entre les fenêtres : ${gapDp.toInt()} dp", style = MaterialTheme.typography.titleSmall)
        Slider(value = gapDp, onValueChange = { gapDp = it }, valueRange = 0f..24f)

        // --- Recherche + liste des applis ---
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Rechercher une appli") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(apps.filter { it.label.contains(search, ignoreCase = true) }) { app ->
                val order = selected.indexOf(app.packageName)
                Row(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (order >= 0) selected.remove(app.packageName) else selected.add(app.packageName)
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bmp = remember(app.packageName) { app.icon.toBitmap().asImageBitmap() }
                    Image(bmp, contentDescription = null, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(app.label, Modifier.weight(1f))
                    if (order >= 0) Badge { Text("${order + 1}") }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    Launcher.launch(context, selected.toList(), layout, (gapDp * context.resources.displayMetrics.density).toInt())
                },
                enabled = selected.size >= 2,
                modifier = Modifier.weight(1f)
            ) { Text("Lancer (${selected.size})") }
            OutlinedButton(
                onClick = { showSave = true },
                enabled = selected.size >= 2
            ) { Text("Enregistrer") }
        }
        TextButton(onClick = {
            // Ouvre les options pour développeurs, où activer les fenêtres libres (freeform).
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }) { Text("Ouvrir les options pour développeurs") }
    }

    if (showSave) {
        AlertDialog(
            onDismissRequest = { showSave = false },
            title = { Text("Nom du raccourci") },
            text = {
                OutlinedTextField(value = presetName, onValueChange = { presetName = it }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    if (presetName.isNotBlank()) {
                        presets = presets + Preset(presetName.trim(), layout, selected.toList())
                        PresetStore.save(context, presets)
                        presetName = ""
                    }
                    showSave = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showSave = false }) { Text("Annuler") } }
        )
    }
}
