package com.example.ezinvoice.models

interface OnProductActionListener {
    fun onUpdateClicked(productId: String)
    fun onDeleteClicked(productId: String)
}
