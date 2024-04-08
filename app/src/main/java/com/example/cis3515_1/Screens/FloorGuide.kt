package com.example.cis3515_1.Screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.R
import com.example.cis3515_1.TopNavigationBar

@Composable
fun FloorGuide(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{

    Scaffold(topBar = { TopNavigationBar(onClick = onClick, navController = navController) }, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {

            LazyColumn() {
                item {
                    var scale by remember {mutableStateOf(1f) }
                    var rotation by remember {mutableStateOf(1f) }
                    var offset by remember {mutableStateOf(Offset.Zero)}
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)
                            rotation += rotationChange

                                val extraWidth = (scale - 1) * constraints.maxWidth
                                val extraHeight = (scale - 1) * constraints.maxHeight

                                val maxX = extraWidth / 2
                                val maxY = extraHeight / 2

                                offset = Offset(
                                    x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                                    y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY),
                                )
                            }
                        Image(
                            painter = painterResource(id = R.drawable.floorguidetuj),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    rotationY = rotation
                                    translationX = offset.x
                                    translationY = offset.y }
                                .transformable(state)
                        )
                    }
                }

                item {
                    Image(painter = painterResource(id = R.drawable.maptuj),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}