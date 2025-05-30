package com.example.ezinvoice.models


data class BusinessInfo(
    val _id: String,
    val userId: String, // Foreign key reference to User table
    val name: String,
    val ownername: String,
    val gstin: String?,
    val address: String,
    val email: String,
    val contact: String,
    val website: String?,
    val currency: String,
    val country:String,
    val numberformate: String,
    val dateformate: String,
    val signature: String?,
    val categoryIds:List<String>?,
    val createdAt: String
)

data class BusinessResponse(
    val message: String,
    val business: BusinessInfo,
)
