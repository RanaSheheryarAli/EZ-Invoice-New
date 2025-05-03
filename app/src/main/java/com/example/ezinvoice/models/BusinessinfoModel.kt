package com.example.ezinvoice.models


data class BusinessInfo(
    val _id: String,
    val userId: String,
    val name: String,
    val ownername: String,
    val gstin: String?,
    val address: String,
    val email: String,
    val contact: String,
    val website: String?,
    val country: String,
    val currency: String,
    val numberformate: String,
    val dateformate: String,
    val signature: String?,
    val logoUrl: String?, // ✅ Add logo field
    val categoryIds: List<CatagoryModel>?, // ✅ Correct type
    val createdAt: String // ✅ Required if backend sends this
)


data class BusinessResponse(
    val message: String,
    val business: BusinessInfo,
)
