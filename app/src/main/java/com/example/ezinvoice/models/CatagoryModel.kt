package com.example.ezinvoice.models

data class CatagoryModel(
    var businessId:String,
    var name:String,
    val subcategory:String
)
data class CatagoryResponce(
    val _id: String,        // Unique ID of the category
    val businessId: String, // Business ID for the category
    val name: String,       // ✅ Name field (this was missing in your model)
    val subcategories: List<String> // Assuming subcategories are a list of strings (IDs)
)