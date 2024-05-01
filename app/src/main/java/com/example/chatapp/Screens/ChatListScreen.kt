package com.example.chatapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.CommonRow
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R
import com.example.chatapp.TitleTxt
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.AppColor
import com.example.chatapp.ui.theme.Color1

@Composable
fun ChatListScreen(navController: NavController, vm: LiveChatViewModel) {
    val context= LocalContext.current.applicationContext
    if (vm.inProgressChats.value) {
        CommonProgressBar()
    } else {
        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = { showDialog.value = true }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            vm.onAddChat(it, context = context)
            showDialog.value = false
        }

        Scaffold(
            topBar = {
                TopBar()
            },
            floatingActionButton = {
                TOP_BAR(
                    showDialog = showDialog.value,
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    onAddChat = onAddChat
                )
            },
            content = {
                if (chats.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(it)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chatting_amico),
                            contentDescription = "",
                            modifier = Modifier
                                .size(300.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                        Row(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text(
                                text = "Add New Friends,", fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                color = AppColor

                            )
                            Text(
                                text = " Start chatting, ", fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            )
                        }
                    }
                } else {
                    Row(modifier = Modifier.padding(top = 56.dp)) {
                        LazyColumn() {
                            items(chats) { chat ->
                                val chatUser = if (chat.user1.userId != userData?.userId) {
                                    chat.user1
                                } else {
                                    chat.user2
                                }
                                CommonRow(
                                    imageUrl = chatUser.imageUrl,
                                    name = chatUser.name,
                                    onItemClick = {
                                        chat.chatId?.let {
                                            navigateTo(
                                                navController,
                                                DestinationScreen.SingleChat.createRoute(id = it)
                                            )
                                        }
                                    }
                                ) {

                                }
                            }
                        }
                    }

                }

            },
            bottomBar = {
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.CHAT_LIST,
                    navController = navController
                )
            }
        )
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor)
            .size(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleTxt(txt = "Chat Screen")
    }

}


@Composable
fun TOP_BAR(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    val MinNum = 10
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        }, confirmButton = {
            Button(
                onClick = {
                    onAddChat(addChatNumber.value)
                },
                colors = ButtonDefaults.buttonColors(Color1),
                contentPadding = PaddingValues(
                    10.dp
                ),
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Add Friend",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Black
                )

            }

        }, title = {
            Text(
                text = "Add New Friend Start Chatting ",
                fontSize = 15.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Black,
                color = AppColor
            )
        }, text = {
            OutlinedTextField(
                value = addChatNumber.value,
                onValueChange = {
                    if (addChatNumber.value.length < MinNum) addChatNumber.value = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    unfocusedLeadingIconColor = Color1,
                    focusedLabelColor = AppColor,
                    unfocusedLabelColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = AppColor,
                    unfocusedIndicatorColor = Color.LightGray,
                    unfocusedPlaceholderColor = AppColor,
                    errorTextColor = Color.Red

                ),
                label = { Text(text = "Number", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            )
        }, icon = {
            Image(
                painterResource(id = R.drawable.group_chat_pana),
                contentDescription = "",
                modifier = Modifier.width(300.dp)
            )
        }, containerColor = Color.White
        )
    }
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = AppColor,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 5.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add, contentDescription = "", tint = Color.White
        )
    }
}
