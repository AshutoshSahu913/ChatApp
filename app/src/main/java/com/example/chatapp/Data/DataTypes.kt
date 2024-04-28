package com.example.chatapp.Data


data class UserData(
    var userId: String? = "",
    var userName: String? = "",
    var userNumber: String? = "",
    var userImageUrl: String? = "",
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "userName" to userName,
        "userNumber" to userNumber,
        "userImageUrl" to userImageUrl
    )
}

data class ChatData(
    val chatId: String? = "",
    val user1:ChatUser=ChatUser(),
    val user2:ChatUser= ChatUser(),
    )

data class ChatUser(
    val userId: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = "",

    )


data class Message(
    var sendBy: String? = "",
    var message: String? = "",
    var timeStamp: String? = ""
)