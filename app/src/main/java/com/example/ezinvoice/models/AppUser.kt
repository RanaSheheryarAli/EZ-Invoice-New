package com.example.ezinvoice.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "user")
data class AppUser(
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0, // Auto-generated User ID
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
