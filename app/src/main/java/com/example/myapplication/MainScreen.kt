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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

val xlocations = arrayListOf(330, 100, 200, 300, 40, 70, 200, 350)
val ylocations = arrayListOf(50, 600, 150, 350, 500, 100, 50, 550)
val colors = arrayListOf(
    Color(0xFFFFFFFF),
    Color(0xFFFF0000),
    Color(0xFF3498DB),
    Color(0xFF00FF00),
    Color(0xFF0000FF),
    Color(0xFF999999),
    Color(0xFFFFFF00),
    Color(0xFF00FFFF)
)

@Composable
fun MainScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(onCloseDrawer = {
                scope.launch { drawerState.close() }
            }, navController = navController)
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (drawerState.isOpen) {
                            alpha = 0.5f
                        }
                    }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Schedule ++") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) {
                                            drawerState.close()
                                        } else {
                                            drawerState.open()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    ZoomableBox(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    )
}

@Composable
fun ZoomableBox(modifier: Modifier = Modifier) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Surface(
        modifier = modifier.fillMaxSize(),
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
            for(i:Int in 1..<xlocations.size) DrawLineBetweenPoints(
                startX = ((xlocations[i-1]+30/(scale+1)) * 2.63).toFloat(),
                startY = ((ylocations[i-1]+30/(scale+1)) * 2.63).toFloat(),
                endX = ((xlocations[i]+30/(scale+1)) * 2.63).toFloat(),
                endY = ((ylocations[i]+30/(scale+1)) * 2.63).toFloat(),
                lineNumber = i
            )
            for(i:Int in 0..<xlocations.size) PointLayout(pointNumber = i , size = scale)
        }
    }
}

@Composable
fun PointLayout(size:Float, pointNumber:Int){
    val xloc = xlocations[pointNumber]
    val yloc = ylocations[pointNumber]
    val color = colors[pointNumber]
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(pointNumber.toLong() * 400)
        while (true) {
            scope.launch {
                launch { scale.animateTo(3f, animationSpec = tween(durationMillis = 2000)) }
                launch { alpha.animateTo(0f, animationSpec = tween(durationMillis = 2000)) }
            }
            delay(1500 + (xlocations.size*400).toLong())

            scale.snapTo(1f)
            alpha.snapTo(1f)
        }
    }

    Box(
        modifier = Modifier
            .offset(xloc.dp, yloc.dp)
            .size((60 / (size + 1)).dp, (60 / (size + 1)).dp)
            .background(color, CircleShape)
    ){
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value)
                .alpha(alpha.value)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun DrawLineBetweenPoints(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    lineNumber: Int
) {
    val color = colors[lineNumber]
    Canvas(
        modifier = Modifier
    ) {
        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }
}