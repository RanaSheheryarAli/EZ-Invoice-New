package com.example.ezinvoice.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "user")
data class AppUser(
    @PrimaryKey()
    var userId: String, // Auto-generated User ID
    @SerializedName("username") var username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("businessID") val businessID: String
)
data class SignupResponse(
    val message: String,
    val newUser: NewUser
)

data class NewUser(
    val _id: String,
    val username: String,
    val email: String
)
