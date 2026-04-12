package com.example.ezinvoice.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.databinding.ScannedproductslayoutBinding
import com.example.ezinvoice.models.ScannedProduct
import java.text.NumberFormat
import java.util.*

class SaleAdaptor(
    private val products: List<ScannedProduct>
) : RecyclerView.Adapter<SaleAdaptor.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ScannedproductslayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ScannedproductslayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val scannedProduct = products[position]
        val product = scannedProduct.product
        val quantity = scannedProduct.quantity

        val salePrice = product.saleprice
        val discount = product.salepriceDiscount
        val taxPercent = product.Texes


        val discountedPrice = salePrice - discount
        val subtotal1 = product.saleprice * quantity
        val subtotal2 = discountedPrice * quantity
        val taxAmount = (salePrice * taxPercent / 100) * quantity
        val finalTotal = subtotal2 + taxAmount

        holder.binding.apply {
            productIndex.text = "#${position + 1}"
            productName.text = product.name
            productTotalPrice.text = "Rs ${product.saleprice}"

            quantityTextView.text = quantity.toString()
            saleprice.text = formatPrice(salePrice)
            totalprice.text = formatPrice(subtotal1)

            discounttxt.text = "Discount:"
            discounttxt.text = "Rs ${formatPrice(discount * quantity)}"

            taxtxt.text = "Tax:"
            tax.text = "Rs ${formatPrice(taxAmount)}"

            totalxt.text = "Total:"
            total.text = "Rs ${formatPrice(finalTotal)}"
        }
    }

    private fun formatPrice(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return formatter.format(amount)
    }
}
