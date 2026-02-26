package com.blitz.inklings.ui

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke

@Composable
fun DrawingScreen() {
    var finishedStrokes by remember { mutableStateOf<List<Stroke>>(emptyList()) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(5f) }

    val currentBrush = remember(selectedColor, brushSize) {
        Brush.createWithComposeColor(
            family = StockBrushes.pressure(),
            colorIntArgb = selectedColor.toArgb(),
            size = brushSize,
            epsilon = 0.1f
        )
    }

    val canvasStrokeRenderer = remember { CanvasStrokeRenderer.create() }

    Scaffold(
        topBar = {
            DrawingToolbar(
                selectedColor = selectedColor,
                selectedSize = brushSize,
                onColorSelected = { selectedColor = it },
                onSizeSelected = { brushSize = it },
                onClear = { finishedStrokes = emptyList() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Dry layer: render completed strokes
            Canvas(modifier = Modifier.fillMaxSize()) {
                val identityMatrix = Matrix()
                finishedStrokes.forEach { stroke ->
                    canvasStrokeRenderer.draw(
                        stroke = stroke,
                        canvas = drawContext.canvas.nativeCanvas,
                        strokeToScreenTransform = identityMatrix
                    )
                }
            }

            // Wet layer: capture touch input and render the active stroke
            InProgressStrokes(
                modifier = Modifier.fillMaxSize(),
                defaultBrush = currentBrush,
                onStrokesFinished = { newStrokes ->
                    finishedStrokes = finishedStrokes + newStrokes
                }
            )
        }
    }
}
