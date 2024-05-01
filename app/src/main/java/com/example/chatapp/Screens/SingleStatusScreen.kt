package com.example.chatapp.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImg
import com.example.chatapp.CommonRow
import com.example.chatapp.LiveChatViewModel

enum class State {
    INITIAL, ACTIVE, COMPLETED
}

@Composable
fun SingleStatusScreen(navController: NavController, vm: LiveChatViewModel, userId: String) {
    val statuses = vm.status.value.filter {
        it.user.userId == userId
    }

    val user = vm.userData.value?.userId
    val scale = remember {
        mutableStateOf(1f)
    }
    val rotationState = remember {
        mutableStateOf(1f)
    }
    if (statuses.isNotEmpty()) {
        val currentStatus = remember {
            mutableStateOf(0)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {

            Row(modifier = Modifier
                .fillMaxSize()
                .clip(RectangleShape)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, rotation ->
                        scale.value *= zoom
                        rotationState.value += rotation
                    }
                }) {
                CommonImg(
                    data = statuses[currentStatus.value].imageUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = maxOf(.5f, minOf(3f, scale.value)),
                            scaleY = maxOf(.5f, minOf(3f, scale.value)),
                            rotationZ = rotationState.value
                        ),
                    contentScale = ContentScale.Fit
                )

            }

            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    statuses.forEachIndexed { index, status ->
                        CustomProgressIndicator(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .padding(1.dp),
                            state = if (currentStatus.value < index) State.INITIAL else if (currentStatus.value == index) State.ACTIVE else State.COMPLETED
                        ) {
                            if (currentStatus.value < statuses.size - 1) {
                                currentStatus.value++
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }

                }
                if (user == statuses[currentStatus.value].user.userId) {
                    CommonRow(
                        imageUrl = statuses[currentStatus.value].user.imageUrl,
                        name = "My Status",
                        onItemClick = { /*TODO*/ }) {
                    }
                } else {
                    CommonRow(
                        imageUrl = statuses[currentStatus.value].user.imageUrl,
                        name = statuses[currentStatus.value].user.name,
                        onItemClick = { /*TODO*/ }) {
                    }
                }
            }
        }
    }
}

@Composable
fun CustomProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit) {

    var process = if (state == State.INITIAL) 0f else 1f
    if (state == State.ACTIVE) {
        val toggleState = remember {
            mutableStateOf(false)
        }
        LaunchedEffect(key1 = toggleState) {
            toggleState.value = true
        }
        val p: Float by animateFloatAsState(targetValue = if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = { onComplete.invoke() })
        process = p
    }

    LinearProgressIndicator(modifier = modifier, color = Color.Red, progress = process)

}
