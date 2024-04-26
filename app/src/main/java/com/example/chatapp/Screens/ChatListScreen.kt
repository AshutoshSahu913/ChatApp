package com.example.chatapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R

@Composable
fun ChatListScreen(navController: NavController, vm: LiveChatViewModel) {

    Text(text = "Chat Screen")


    Image(
        painter = painterResource(id = R.drawable.chatting_amico),
        contentDescription = "",
        modifier = Modifier
            .fillMaxWidth()

            .padding(start = 40.dp, end = 40.dp, top = 20.dp)
    )

    BottomNavigationMenu(selectedItem = BottomNavigationItem.CHAT_LIST,navController =navController )

    

}