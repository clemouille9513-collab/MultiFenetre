package com.exemple.multifenetre

import android.graphics.Rect

/** Dispositions possibles pour placer plusieurs fenêtres à l'écran. */
enum class WindowLayout(val label: String) {
    GRID("Grille"),
    COLUMNS("Colonnes"),
    ROWS("Lignes"),
    MAIN_PLUS_SIDE("1 grande + petites");

    /** Calcule un rectangle (en pixels) par fenêtre. [gap] = espace entre fenêtres. */
    fun compute(count: Int, width: Int, height: Int, gap: Int): List<Rect> {
        if (count <= 0) return emptyList()
        return when (this) {
            COLUMNS -> split(count, width, height, gap, vertical = true)
            ROWS -> split(count, width, height, gap, vertical = false)
            GRID -> grid(count, width, height, gap)
            MAIN_PLUS_SIDE -> mainPlusSide(count, width, height, gap)
        }
    }

    private fun split(count: Int, w: Int, h: Int, gap: Int, vertical: Boolean): List<Rect> {
        val total = if (vertical) w else h
        val size = (total - gap * (count - 1)) / count
        return List(count) { i ->
            val start = i * (size + gap)
            if (vertical) Rect(start, 0, start + size, h) else Rect(0, start, w, start + size)
        }
    }

    private fun grid(count: Int, w: Int, h: Int, gap: Int): List<Rect> {
        val cols = Math.ceil(Math.sqrt(count.toDouble())).toInt()
        val rows = Math.ceil(count / cols.toDouble()).toInt()
        val cw = (w - gap * (cols - 1)) / cols
        val ch = (h - gap * (rows - 1)) / rows
        return List(count) { i ->
            val c = i % cols
            val r = i / cols
            val left = c * (cw + gap)
            val top = r * (ch + gap)
            Rect(left, top, left + cw, top + ch)
        }
    }

    private fun mainPlusSide(count: Int, w: Int, h: Int, gap: Int): List<Rect> {
        if (count == 1) return listOf(Rect(0, 0, w, h))
        val mainW = (w * 0.6f).toInt()
        val sideW = w - mainW - gap
        val others = count - 1
        val sideH = (h - gap * (others - 1)) / others
        val rects = mutableListOf(Rect(0, 0, mainW, h))
        for (i in 0 until others) {
            val top = i * (sideH + gap)
            rects += Rect(mainW + gap, top, mainW + gap + sideW, top + sideH)
        }
        return rects
    }
}
