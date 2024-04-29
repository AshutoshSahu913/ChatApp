package com.example.chatapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.chatapp.ui.theme.AppColor

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun CommonProgressBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.background(Color.Transparent)
    ) {
        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, trackColor = AppColor)
    }
}


@Composable
fun CheckSignedIn(viewModel: LiveChatViewModel, navController: NavController) {
    val alreadySignIn = remember {
        mutableStateOf(false)
    }
    val signIn = viewModel.signIn.value
    if (signIn && !alreadySignIn.value) {
        alreadySignIn.value = true
        navController.navigate(DestinationScreen.ChatList.route)
        {
            popUpTo(0)
        }
    }

}

@Composable
fun CommonImg(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    Log.d("DATA", "CommonImg: $data")
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}


@Composable
fun CommonRow(
    imageUrl: String?,
    name: String?,
    onItemClick: () -> Unit?,
    onImgClick: () -> Unit?,
//    time: Long?
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            Color.White
        ), shape = RoundedCornerShape(10.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick.invoke()
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            CommonImg(data = imageUrl, modifier = Modifier
                .padding(8.dp)

                .size(70.dp)
                .clip(CircleShape)
                .clickable {
                    onItemClick.invoke()
                }
            )
//            Column {

            Text(
                text = name ?: "----", fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                color = Color.Black
            )
//                Text(
//                    text = time.toString(), fontSize = 10.sp,
//                    fontWeight = FontWeight.Medium,
//                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
//                    color = Color.Black
//                )
//            }



        }
    }

//    Divider(modifier = Modifier.height(1.dp), color = Color.Black)
}

@Composable
fun TitleTxt(txt: String) {
    Text(
        color = Color.White,
        text = txt,
        fontSize = 25.sp,
        fontFamily = FontFamily(
            Font(R.font.montserrat_regular)
        ),
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(horizontal = 10.dp)

    )

}
