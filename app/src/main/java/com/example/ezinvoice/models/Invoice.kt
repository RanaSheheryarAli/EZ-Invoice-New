package com.example.ezinvoice.models

data class Invoice(
    val invoiceNumber: String,
    val date: String,
    val amount: String
)

data class InvoiceRequest(
    val businessId: String,
    val customerName: String,
    val customerPhone: String,
    val products: List<InvoiceProduct>,
    val totalAmount: Double
)

data class InvoiceProduct(
    val productId: String,
    val quantity: Int,
    val price: Double
)

data class InvoiceResponse(
    val message: String,
    val invoice: Any
)


data class InvoiceCountResponse(
    val count: Int
)



data class InvoiceDisplay(
    val _id: String,
    val date: String,
    val totalAmount: Double
)

data class CustomerDisplay(
    val name: String,
    val phone: String
)

data class InvoiceProductDisplay(
    val productId: ProductDisplay,
    val quantity: Int,
    val price: Double
)

data class ProductDisplay(
    val name: String
)
