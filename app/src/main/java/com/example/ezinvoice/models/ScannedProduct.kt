package com.example.ezinvoice.models

import java.io.Serializable

data class ScannedProduct(
    val product: ProductResponse,
    var quantity: Int = 1 // default quantity 1
) : Serializable