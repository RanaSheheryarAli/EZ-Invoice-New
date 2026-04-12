package com.example.ezinvoice.models

import java.io.Serializable

data class CatagoryModel(
    var businessId:String,
    var name:String,
    val subcategory:String
): Serializable
data class CatagoryResponce(
    val _id: String,        // Unique ID of the category
    val businessId: String, // Business ID for the category
    val name: String,       // ✅ Name field (this was missing in your model)
    val subcategories: List<SubCategoryResponse> // Assuming subcategories are a list of strings (IDs)
): Serializable
data class SubCatagoryModel(
    var categoryId:String,
    val name: String,
    var products:List<ProductModel>
)
data class SubCategoryResponse(
    val _id: String,                   // Unique ID of the subcategory
    val categoryId: String,           // Parent category ID
    val name: String,                 // Name of the subcategory
    val products:  List<String>      // List of products under this subcategory
)

data class AddProductRequest(
    val businessId: String,
    val categoryId: String,
    val subcategoryId: String,
    val name: String,
    val barcode: String,
    val saleprice: Double,
    val salepriceDiscount: Double,
    val purchaceprice: Double,
    val Texes: Double,
    val stock: Int,
    val date: String,
    val minstock: Int,
    val itemlocation: String
)


data class ProductModel(
    val businessId: String,
    val categoryId: CatagoryModel?,
    val subcategoryId: SubCatagoryModel?,
    val name: String,
    val barcode: String,
    val saleprice: Double,
    val salepriceDiscount: Double,
    val purchaceprice: Double,
    val Texes: Double,
    val stock: Int,
    val date: String,
    val minstock: Int,
    val itemlocation: String
) : Serializable


data class ProductResponse(
    val _id: String,
    val businessId: String,
    val categoryId: Catagoryid,
    val subcategoryId: SubCatagoryid,
    val name: String,
    val barcode: String,
    val saleprice: Double,
    val salepriceDiscount: Double,
    val purchaceprice: Double,
    val Texes: Double,
    val stock: Int,
    val date: String,
    val minstock: Int,
    val itemlocation: String
) : Serializable


data class Catagoryid(
    val _id: String,
    val name: String
) : Serializable
data class SubCatagoryid(
    val _id: String,
    val name: String
) : Serializable