package com.example.ezinvoice.models


//@Entity(
//    tableName = "business_info",
//    foreignKeys = [
//        ForeignKey(
//            entity = AppUser::class,
//            parentColumns = ["userId"],
//            childColumns = ["userId"],
//            onDelete = ForeignKey.CASCADE // Deletes business info if the user is deleted
//        )
//    ],
//    indices = [Index(value = ["userId"])]
//)
data class BusinessInfo(
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
    val categoryIds:List<String>?
)
