package com.example.chatapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.DestinationScreen
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.AppColor
import com.example.chatapp.ui.theme.Color1

enum class BottomNavigationItem(
    var icon: ImageVector,
//    var name1: String,
    var destinationScreen: DestinationScreen
) {
    CHAT_LIST(
        Icons.Default.MailOutline/*, "Chat"*/,
        destinationScreen = DestinationScreen.ChatList
    ),
    STATUS_LIST(
        Icons.Default.SlowMotionVideo,
        /*"Status",*/
        destinationScreen = DestinationScreen.Status
    ),
    PROFILE_LIST(Icons.Default.Person/*, "Profile"*/, destinationScreen = DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {

    Box(contentAlignment = Alignment.BottomCenter) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color1)

        ) {

            for (item in BottomNavigationItem.entries) {
                Image(
                    imageVector = item.icon, contentDescription = "iconImg",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(40.dp)
                        .weight(1f)
                        .clickable {
                            navigateTo(navController, item.destinationScreen.route)
                        }, colorFilter = if (item == selectedItem)
                        ColorFilter.tint(color = Color.White)
                     else
                        ColorFilter.tint(Color.DarkGray)
                )
            }
        }
    }
}

