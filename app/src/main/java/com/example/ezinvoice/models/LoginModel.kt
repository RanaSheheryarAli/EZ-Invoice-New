package com.example.ezinvoice.models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
data class LoginResponse(
    val success: String,
    val token: String,
    val user: User
) {
    data class User(
        val id: String,
        val username: String,
        val email: String,
        val token: String
    )
}