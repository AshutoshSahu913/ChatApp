package com.example.chatapp.Screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.chatapp.CommonImg
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.Data.Message
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R
import com.example.chatapp.ui.theme.AppColor
import com.example.chatapp.ui.theme.Color1
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SingleChatScreen(
    navController: NavHostController,
    vm: LiveChatViewModel, chatId: String) {
    val context = LocalContext.current

    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        vm.onSendReply(chatID = chatId, message = reply)
        reply = ""
    }
    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    val showDialog = remember {
        mutableStateOf(false)
    }
    val onFabClick: () -> Unit = { showDialog.value = true }
    val onDismiss: () -> Unit = { showDialog.value = false }

    LaunchedEffect(key1 = Unit) {
        vm.populateMessages(chatId)
    }

    BackHandler {
        vm.depopulateMessages()
    }
    val chatMessages = vm.chatMessages
    Scaffold(
            topBar = {
                TOP_BAR(
                    name = chatUser.name ?: "",
                    imageUrl = chatUser.imageUrl ?: "",
                    number = chatUser.number ?: "",
                    onBack = {
//                    navigateTo(
//                        navController = navController,
//                        route = DestinationScreen.ChatList.route
//                    )
                        navController.popBackStack()
                        vm.depopulateMessages()
                    },
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    showDialog = showDialog.value, context = context
                )
//            TopBar(
//            )
            },
            bottomBar = {
                ReplyBox(
                    reply = reply,
                    onReplyChange = { reply = it },
                    onSendReply = onSendReply
                )
            },
            content = {
                Box(modifier = Modifier.fillMaxSize()) {

                    MessageBox(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        chatMessages = chatMessages.value,
                        currentUserId = myUser?.userId ?: ""
                    )
                }
            },
        )

}


@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {

    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) AppColor else Color1
            var lastDisplayedDay: String? = null // Keep track of the last displayed day

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
//              This for display Date
                val messageDate = Date(msg.timeStamp)
                val currentDay =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(messageDate)

                if (lastDisplayedDay != currentDay) {
                    // If the day has changed, display it
                    Text(
                        text = currentDay,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                    lastDisplayedDay = currentDay // Update the last displayed day
                }
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(14.dp),
                    color = Color.White,
                )
                val timeStamp =
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timeStamp))

                Text(
                    text = timeStamp ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(5.dp), color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}


@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column {
        Divider()
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceAround
        ) {
            OutlinedTextField(
                value = reply,
                onValueChange = {
                    onReplyChange.invoke(it)
                },

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
            )

            Card(shape = CircleShape) {

                Image(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            onSendReply.invoke()
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TOP_BAR(
    name: String,
    number: String,
    imageUrl: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onFabClick: () -> Unit, onBack: () -> Unit, context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor)
            .size(56.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .padding(6.dp)
                .clickable {
                    onBack.invoke()
                },
            tint = Color.White,
        )
        ElevatedCard(
            onClick = {
                onFabClick.invoke()
            }, modifier = Modifier
                .clip(CircleShape)
                .size(41.dp)
                .background(Color1)
        ) {
            CommonImg(
                data = imageUrl, modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
            )
        }

        Text(
            color = Color.White,
            text = name,
            fontSize = 18.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        )

        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "",
            modifier = Modifier
                .padding(end = 10.dp)
                .wrapContentSize(Alignment.Center)
                .background(Color.Transparent)
                .clickable {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$number")
                    context.startActivity(intent)
                },
            tint = Color.White

        )
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
        }, confirmButton = {
            Button(
                onClick = {
                    onDismiss.invoke()
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
                    text = "Cancel",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Black
                )
            }
        }, icon = {
            val painter = rememberImagePainter(data = imageUrl)
            Image(
                painter,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        },
            text = {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,

                        ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 10.dp), tint = AppColor
                        )
                        Text(
                            text = name,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    }

                    Spacer(modifier = Modifier.size(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clickable {

                                },
                            tint = AppColor
                        )
                        Text(
                            text = number,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    }
                }

            }, containerColor = Color.White
        )
    }
}