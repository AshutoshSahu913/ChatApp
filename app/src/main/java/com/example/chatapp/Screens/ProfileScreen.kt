package com.example.chatapp.Screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.chatapp.LiveChatViewModel

@Composable
fun ProfileScreen(navController: NavHostController, vm: LiveChatViewModel) {
    Text(text = "Profile Screen")

    BottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE_LIST, navController =navController )

}