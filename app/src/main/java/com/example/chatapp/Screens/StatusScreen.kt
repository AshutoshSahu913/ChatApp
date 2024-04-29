package com.example.chatapp.Screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.CommonRow
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R
import com.example.chatapp.TitleTxt
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.AppColor

@Composable
fun StatusScreen(navController: NavHostController, vm: LiveChatViewModel) {
    val context = LocalContext.current.applicationContext
    if (vm.inProgressStatus.value) {
        CommonProgressBar()
    } else {
        val statuses = vm.status.value
//        Log.d(
//            "STATUS",
//            "get form Server Status-------------------------------------------------------------------------------- "
//        )
        val userData = vm.userData.value
//        filter the statuses
        val myStatuses = statuses.filter {
            it.user.userId == userData?.userId
        }
        val otherStatuses = statuses.filter {
            it.user.userId != userData?.userId
        }

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    vm.uploadStatus(uri)
                    Log.d(
                        "STATUS",
                        "getImg-------------------------------------------------------------------------------- "
                    )
                }
            }

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColor)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TitleTxt(txt = "Status")
                }
            },
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            },

            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(it)
                ) {
                    //check status if not available show empty txt else show status
                    if (statuses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.conversation_rafiki),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(300.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            )
                            Row(modifier = Modifier.padding(bottom = 10.dp)) {
                                Text(
                                    text = "No Status,",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = Color.Black

                                )
                                Text(
                                    text = " No Memories ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = AppColor
                                )
                            }
                        }

                    } else {
                        if (myStatuses.isNotEmpty()) {
                            /*Log.d(
                                "MYSTATUS",
                                "StatusScreen: my status m aa raha h ${myStatuses[0].user.name}"
                            )
                            Toast.makeText(
                                context,
                                "${myStatuses[0].user.name} ka status aa raha ",
                                Toast.LENGTH_SHORT
                            ).show()*/
                            //only for my status

                            Text(
                                text = "My Status",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
                                ,color= Color.Black
                            )
                            CommonRow(
                                imageUrl = myStatuses[0].user.imageUrl,
                                name = "My Status",
                                onItemClick = {
                                    navigateTo(
                                        navController = navController,
                                        DestinationScreen.SingleStatus.createRoute(myStatuses[0].user.userId!!)
                                    )
                                }
                            ) {

                            }
//                            Divider(modifier = Modifier.padding(vertical = 2.dp))

                            Text(
                                text = "Friends Status",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
                                ,color= Color.Black
                            )
                            //find unique user
                            val uniqueUsers = otherStatuses.map { st ->
                                st.user
                            }.toSet().toList()

//                            Log.d(
//                                "UNIQUE",
//                                "StatusScreen: ${uniqueUsers.size} ke status bhi aa rha h "
//                            )
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(uniqueUsers) { new_user ->
//                                    Log.d(
//                                        "MYSTATUS",
//                                        "StatusScreen:  other ka bhi aa raha h ${new_user.name}"
//                                    )
//                                    Toast.makeText(
//                                        context,
//                                        "${new_user.name} ke status aa raha ",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                    CommonRow(
                                        imageUrl = new_user.imageUrl,
                                        name = new_user.name,
                                        onItemClick = {
                                            navigateTo(
                                                navController = navController,
                                                route = DestinationScreen.SingleStatus.createRoute(
                                                    new_user.userId!!
                                                )
                                            )
                                        }

                                    ) {

                                    }
                                }
                            }
                        }
                    }
                }
            },


            bottomBar = {
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.STATUS_LIST, navController = navController
                )
            }

        )
    }


}

@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = AppColor,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 5.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ModeEdit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}