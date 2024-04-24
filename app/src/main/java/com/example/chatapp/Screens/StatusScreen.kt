package com.example.chatapp.Screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.chatapp.LiveChatViewModel

@Composable
fun StatusScreen(navController: NavHostController, vm: LiveChatViewModel) {
    Text(text = "Status Screen")

    BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUS_LIST, navController =navController )

}