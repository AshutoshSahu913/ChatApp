package com.example.chatapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LiveChatViewModel
import com.example.chatapp.R
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.AppColor
import com.example.chatapp.ui.theme.Color1

@Composable
fun WelcomeScreen(navController: NavHostController, vm: LiveChatViewModel) {

    Box() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    Color.White
                ),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {

            Image(
                painter = painterResource(id = R.drawable.conversation_rafiki),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 40.dp)
            )

            Text(
                text = "CHAT APP", fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                color = AppColor,
                modifier = Modifier.shadow(elevation = 40.dp, spotColor = Color.Black)
            )


            Spacer(modifier = Modifier.size(10.dp))
            Row(modifier = Modifier.padding(bottom = 100.dp)) {


                Text(
                    text = "Start chatting,", fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    color = AppColor

                )
                Text(
                    text = " start connecting, ", fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                )
                Text(
                    text = "start smiling", fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    color = Color1,
                )
            }


            Button(
                onClick = {
                    navigateTo(navController, DestinationScreen.Login.route)
                },
                colors = ButtonDefaults.buttonColors(Color1),
                contentPadding = PaddingValues(
                    start = 100.dp,
                    end = 100.dp,
                    top = 20.dp,
                    bottom = 20.dp
                ),
                modifier = Modifier
                    .padding(vertical = 50.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.Bottom), shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Welcome",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                    fontWeight = FontWeight.Black
                )
            }

        }
    }
}