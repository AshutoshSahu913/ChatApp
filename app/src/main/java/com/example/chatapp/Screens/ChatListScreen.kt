package com.example.chatapp.Screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.chatapp.LiveChatViewModel

@Composable
fun ChatListScreen(navController: NavController, vm: LiveChatViewModel) {

    Text(text = "Chat Screen")
    
    BottomNavigationMenu(selectedItem = BottomNavigationItem.CHAT_LIST,navController =navController )

    

}