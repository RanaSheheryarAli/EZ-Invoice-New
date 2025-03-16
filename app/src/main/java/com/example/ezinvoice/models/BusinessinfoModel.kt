package com.example.ezinvoice.models

import androidx.room.DeleteTable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "business_info",
    foreignKeys = [
        ForeignKey(
            entity = AppUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Deletes business info if the user is deleted
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class BusinessInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Foreign key reference to User table
    val businessName: String,
    val ownerName: String,
    val gstin: String?,
    val address: String,
    val email: String,
    val phoneNumber: String,
    val website: String?,
    val currency: String,
    val numberFormat: String,
    val dateFormat: String,
    val signature: String? // Store as Base64 string or file path
)
