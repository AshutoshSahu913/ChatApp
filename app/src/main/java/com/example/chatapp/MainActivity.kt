package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.Screens.ChatListScreen
import com.example.chatapp.Screens.LoginScreen
import com.example.chatapp.Screens.ProfileScreen
import com.example.chatapp.Screens.SignUpScreen
import com.example.chatapp.Screens.SingleChatScreen
import com.example.chatapp.Screens.SingleStatusScreen
import com.example.chatapp.Screens.StatusScreen
import com.example.chatapp.Screens.WelcomeScreen
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(var route: String) {
    data object Welcome : DestinationScreen("welcome")
    data object SignUp : DestinationScreen("signup")
    data object Login : DestinationScreen("login")
    data object Profile : DestinationScreen("profile")
    data object ChatList : DestinationScreen("chatList")
    data object Status : DestinationScreen("status")
    data object SingleChat : DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id: String) = "singlechat/$id"
    }

    data object SingleStatus : DestinationScreen("singleStatus/{userId}") {
        fun createRoute(userId: String) = "singleStatus/$userId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    ChatAppNavigation()
                }
            }
        }
    }


    @Composable
    fun ChatAppNavigation() {

        val navController = rememberNavController()
        val vm = hiltViewModel<LiveChatViewModel>()

        var navHost = NavHost(
            navController = navController,
            startDestination = DestinationScreen.Welcome.route
        ) {


            composable(DestinationScreen.Welcome.route) {
                WelcomeScreen(
                    navController, vm)
            }

            composable(DestinationScreen.SignUp.route) {
                SignUpScreen(navController, vm)
            }

            composable(DestinationScreen.Login.route) {
                LoginScreen(navController, vm)
            }

            composable(DestinationScreen.ChatList.route) {
                ChatListScreen(navController,vm)
            }


            composable(DestinationScreen.SingleChat.route) {
                val    chatId=it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController = navController, vm = vm, chatId = chatId)
                }
            }

            composable(DestinationScreen.Status.route) {
                StatusScreen(navController,vm)
            }

            composable(DestinationScreen.Profile.route) {
                ProfileScreen(navController,vm)
            }

            composable(DestinationScreen.SingleStatus.route) {
                SingleStatusScreen(navController, vm)
            }
        }

    }
}

