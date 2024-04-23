package com.example.chatapp

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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

        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, trackColor = Color.Black)

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