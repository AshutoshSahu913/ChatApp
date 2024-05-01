package com.example.chatapp.Screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.CheckSignedIn
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.AppColor
import com.example.chatapp.ui.theme.Color1

@Composable
fun LoginScreen(navController: NavController, vm: LiveChatViewModel) {
    CheckSignedIn(viewModel = vm, navController = navController)

    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val context = LocalContext.current.applicationContext
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(
                Color.White
            )
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        Image(
            painter = painterResource(id = R.drawable.computer_login_pana),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
        )

        ElevatedCard(
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(
                10.dp,
            ),
            modifier = Modifier
                .padding(10.dp)
                .background(Color.White),
            colors = CardDefaults.cardColors(Color.White)
        ) {

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = {
                    Text(
                        text = "Email",
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
                        imageVector = Icons.Default.Email,
                        contentDescription = "email icon",
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 10.dp),
//                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.size(10.dp))
            OutlinedTextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                },
                label = { Text(text = "Password", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
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
                        imageVector = Icons.Default.Lock,
                        contentDescription = "password icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 30.dp),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            )
        }
        Spacer(modifier = Modifier.size(10.dp))

        Button(
            onClick = {
                if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                    vm.loginIn(email = email.value, password = password.value)
//                    navigateTo(navController, DestinationScreen.ChatList.route)
//                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT)
//                        .show()
                } else {
                    Toast.makeText(context, "Enter Email and password", Toast.LENGTH_SHORT).show()
                }
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
            if (vm.inProgress.value) {
                Text(
                    text = "",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Black
                )
                CommonProgressBar()
            } else {
                Text(
                    text = "Login",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.size(10.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "New User ? ",
                fontSize = 13.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
            )
            Text(
                text = "Register", fontSize = 15.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = AppColor, modifier = Modifier.clickable {
                    navigateTo(navController, DestinationScreen.SignUp.route)
                }
            )
        }
    }
}