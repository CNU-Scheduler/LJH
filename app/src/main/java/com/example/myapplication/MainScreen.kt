package com.example.myapplication


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

val xlocations = arrayListOf(330, 100, 200, 300, 40, 70, 200, 350)
val ylocations = arrayListOf(50, 600, 150, 350, 500, 100, 50, 550)

@Composable
fun ZoomableBox() {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onGloballyPositioned { layoutCoordinates ->
                    boxSize = layoutCoordinates.size
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom

                        val maxXOffset = (boxSize.width.toDp() / 2 * (scale - 1)).toPx()
                        val maxYOffset = (boxSize.height.toDp() / 2 * (scale - 1)).toPx()

                        offsetX = (offsetX + pan.x).coerceIn(-abs(maxXOffset), abs(maxXOffset))
                        offsetY = (offsetY + pan.y).coerceIn(-abs(maxYOffset), abs(maxYOffset))

                        scale = scale.coerceIn(1f, 4f)
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        ) {
            for(i:Int in 0..<xlocations.size) PointLayout(pointNumber = i , size = scale)
            for(i:Int in 1..<xlocations.size) DrawLineBetweenPoints(
                startX = ((xlocations[i-1]+15/scale) * 2.66).toFloat(),
                startY = ((ylocations[i-1]+15/scale) * 2.66).toFloat(),
                endX = ((xlocations[i]+15/scale) * 2.66).toFloat(),
                endY = ((ylocations[i]+15/scale) * 2.66).toFloat()
            )
        }
    }
}

@Composable
fun PointLayout(size:Float, pointNumber:Int){
    val xloc = xlocations[pointNumber]
    val yloc = ylocations[pointNumber]
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    // Coroutine scope for animations
    val scope = rememberCoroutineScope()

    // Automatically start the animation and repeat it periodically
    LaunchedEffect(Unit) {
        delay(pointNumber.toLong() * 400)
        while (true) {
            // Start the animation
            scope.launch {
                launch { scale.animateTo(3f, animationSpec = tween(durationMillis = 2000)) }
                launch { alpha.animateTo(0f, animationSpec = tween(durationMillis = 2000)) }
            }
            delay(1500 + (xlocations.size*400).toLong()) // Wait for the animation to complete

            // Immediately reset to initial state
            scale.snapTo(1f)
            alpha.snapTo(1f)
        }
    }

    Box(
        modifier = Modifier
            .offset(xloc.dp, yloc.dp)
            .size((30 / size).dp, (30 / size).dp)
            .background(Color(0xFF3498DB), CircleShape)
    ){
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value)
                .alpha(alpha.value)
                .background(Color(0xFF3498DB), CircleShape)
        )
    }
}

@Composable
fun DrawLineBetweenPoints(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    lineColor: Color = Color(0xFF3498DB),
    lineWidth: Float = 5f
) {
    Canvas(
        modifier = Modifier
    ) {
        drawLine(
            color = lineColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }
}