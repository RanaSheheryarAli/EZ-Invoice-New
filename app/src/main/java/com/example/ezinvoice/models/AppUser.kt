package com.example.ezinvoice.models

import com.google.gson.annotations.SerializedName

data class AppUser(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
