package com.example.chatapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.chatapp.ui.theme.AppColor
import kotlin.math.log

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun CommonProgressBar() {
    Row(/*modifier = Modifier
        .alpha(0.5f)
        .background(Color.LightGray)
        .clickable(enabled = false) {}
        .fillMaxSize(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
*/
    ) {
        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, trackColor = AppColor)
    }
}


@Composable
fun CheckSignedIn(viewModel: LiveChatViewModel, navController: NavController) {
    val alreadySignIn = remember {
        mutableStateOf(false)
    }
    val signIn = viewModel.signIn.value
    if (signIn && !alreadySignIn.value) {
        alreadySignIn.value = true
        navController.navigate(DestinationScreen.ChatList.route)
        {
            popUpTo(0)
        }
    }

}

@Composable
fun CommonImg(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    Log.d("DATA", "CommonImg: $data")
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}
