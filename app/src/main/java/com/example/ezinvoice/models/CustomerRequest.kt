package com.example.ezinvoice.models

data class CustomerRequest(
    val businessId: String,
    val name: String,
    val phone: String
)
data class CustomerResponse(
    val _id: String,
    val name: String,
    val phone: String,
    val businessId: String,
    val invoices: List<String>,
    val createdAt: String,
    val updatedAt: String

)
