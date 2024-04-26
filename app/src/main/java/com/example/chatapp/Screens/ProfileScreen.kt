package com.example.chatapp.Screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chatapp.CommonImg
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.AppColor
import com.example.chatapp.ui.theme.Color1

@Composable
fun ProfileScreen(navController: NavHostController, vm: LiveChatViewModel) {

    val userData = vm.userData.value

    var name by rememberSaveable {
        mutableStateOf(userData?.userName ?: "")
    }

    var num by rememberSaveable {
        mutableStateOf(userData?.userNumber ?: "")
    }
//
//    var email by rememberSaveable {
//        mutableStateOf(userData?:"")
//    }


    ProfileContent(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(8.dp),
        vm = vm,
        name = name,
        number = num,
//        email = "",
        onNameChange = { name = it },
        onNumberChange = { num = it },
//        onEmailChange = {},
        onBack = {
            navigateTo(navController = navController, route = DestinationScreen.ChatList.route)
        },
        onSave = {
            vm.createAndUpdateProfile(
                name = name,
                phoneNumber = num
            )
        },
        logout = {
            vm.logout()
            navigateTo(navController = navController, route = DestinationScreen.Login.route)
        }
    )
    BottomNavigationMenu(
        selectedItem = BottomNavigationItem.PROFILE_LIST,
        navController = navController
    )

}

@Composable
fun ProfileContent(
    modifier: Modifier,
    vm: LiveChatViewModel,
    name: String,
    number: String,
//    email: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
//    onEmailChange: (String) -> Unit,
    onBack: () -> Unit, onSave: () -> Unit,
    logout: () -> Unit
) {

    Box(modifier = Modifier.background(Color.White)) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack.invoke() }) {

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Color1
                            )
                            .padding(4.dp)
                            .width(100.dp)
                            .clickable {
                                onBack.invoke()
                            },
                        tint = Color.White
                    )
                }
                Text(
                    text = "Logout",
                    fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 3.dp,
                                bottomStart = 4.dp,
                                topEnd = 4.dp,
                                bottomEnd = 3.dp
                            )
                        )
                        .background(AppColor)
                        .padding(5.dp)
                        .clickable {
                            logout.invoke()
                        }

                )
            }
            val imageUrl = vm.userData.value?.userImageUrl
            Log.d("IMG", "ProfileContent: $imageUrl")
            ProfileImage(imageUrl = imageUrl, vm = vm)
            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "Change Profile Picture",
                fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 3.dp,
                            bottomStart = 4.dp,
                            topEnd = 4.dp,
                            bottomEnd = 3.dp
                        )
                    )
                    .background(AppColor)
                    .padding(5.dp)
            )
            OutlinedTextField(
                value = name, onValueChange = onNameChange,
                label = {
                    Text(
                        text = "Username",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedLeadingIconColor = AppColor,
                    unfocusedLeadingIconColor = Color1,
                    focusedLabelColor = AppColor,
                    unfocusedLabelColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = AppColor,
                    unfocusedIndicatorColor = Color.LightGray,
                    unfocusedPlaceholderColor = AppColor,
                    errorTextColor = Color.Red
                ), leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "username Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp), singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.size(10.dp))
            OutlinedTextField(
                value = number,
                onValueChange = onNumberChange,
                label = {
                    Text(
                        text = "Phone number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedLeadingIconColor = AppColor,
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

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "call icon",
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
//                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.size(20.dp))
            Button(
                onClick = {
                    onSave.invoke()
                },
                colors = ButtonDefaults.buttonColors(Color1),
                contentPadding = PaddingValues(
                    start = 100.dp,
                    end = 100.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                modifier = Modifier.padding(top = 10.dp), shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Save",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Black
                )
                if (vm.inProgress.value) {
                    CommonProgressBar()
                }
            }
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, vm: LiveChatViewModel) {
    Log.d("IMG", "ProfileContent: $imageUrl")
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent())
        { uri ->
            uri?.let {
                vm.uploadProfileImg(uri)
            }
        }
    if (!imageUrl.isNullOrEmpty()) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
                .clickable {
                    // Handle click event, if needed
                    launcher.launch("image/*")
                }
        ) {
            // Display image using CommonImg composable
            CommonImg(
                data = imageUrl,
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
        }
    } else {
        // Handle case when imageUrl is null or empty
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp)
                .clickable {
                    launcher.launch("image/*")
                })
    }

    // Optionally, display progress indicator while image is loading
    if (vm.inProgress.value) {
        CommonProgressBar()
    }
}

