package com.example.ezinvoice.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.databinding.ItemInvoiceRowBinding
import com.example.ezinvoice.models.ScannedProduct

class InvoicePreviewAdaptor(
    private val productList: List<ScannedProduct>
) : RecyclerView.Adapter<InvoicePreviewAdaptor.ViewHolder>() {

    inner class ViewHolder(val binding: ItemInvoiceRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInvoiceRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productList[position]
        val product = item.product
        val qty = item.quantity


        val discount = product.salepriceDiscount
        val tax = product.Texes
        val unitPrice = product.saleprice
        val subtotal = qty * (unitPrice - discount)
        val taxAmount = qty * unitPrice * tax / 100
        val total = subtotal + taxAmount

        holder.binding.apply {
            description.text = product.name
            quantityRate.text = "${qty}x${unitPrice.toInt()}"
            discountRate.text = "Rs ${discount.toInt()}"
            discounttxt.text = "${tax.toInt()}%"
            totatxt.text = "Rs ${total.toInt()}"
        }
    }
}
