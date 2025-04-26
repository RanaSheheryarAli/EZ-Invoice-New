package com.example.ezinvoice.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.databinding.ItemsLayoutBinding
import com.example.ezinvoice.models.ProductResponse

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ProductResponse>()

    fun setItems(newItems: List<ProductResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: ItemsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductResponse) {
            binding.apply {
                // bind product data to views
                // change this according to your xml ids
                // Example:
                binding.textViewItemName.text = product.name
                binding.textViewSalePrice.text = "Rs ${product.saleprice}"
                binding.textViewPurchasePrice.text = "Rs ${product.purchaceprice}"
                binding.textViewStock.text = "${product.stock}"
                binding.textViewCatagory.text=product.categoryId
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemsLayoutBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
