package com.example.ezinvoice.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.R
import com.example.ezinvoice.models.ProductResponse

class Scan_BarcodeAdapter(
    private val products: List<ProductResponse>
) : RecyclerView.Adapter<Scan_BarcodeAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemCodetxt: TextView = itemView.findViewById(R.id.itemCodetxt)
        val itemCode: TextView = itemView.findViewById(R.id.itemCode)
        val valueText: TextView = itemView.findViewById(R.id.valueText)
        val minusButton: TextView = itemView.findViewById(R.id.minusButton)
        val plusButton: TextView = itemView.findViewById(R.id.plusButton)
        val deleteIcon: View = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.itemName.text = product.name
        holder.itemCodetxt.text = "Barcode"
        holder.itemCode.text = product.barcode
        holder.valueText.text = "1" // Default quantity is 1

        var quantity = 1

        holder.plusButton.setOnClickListener {
            quantity++
            holder.valueText.text = quantity.toString()
        }

        holder.minusButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                holder.valueText.text = quantity.toString()
            }
        }

        holder.deleteIcon.setOnClickListener {
            (products as MutableList).removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, products.size)
        }
    }
}
