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