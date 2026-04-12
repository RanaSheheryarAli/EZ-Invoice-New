package com.example.ezinvoice.adaptors

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.Add_Items
import com.example.ezinvoice.ItemsFragment
import com.example.ezinvoice.R
import com.example.ezinvoice.databinding.ItemsLayoutBinding
import com.example.ezinvoice.models.OnProductActionListener
import com.example.ezinvoice.models.ProductResponse

class ItemAdapter(
    private val listener: OnProductActionListener? = null
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ProductResponse>()

    fun setItems(newItems: List<ProductResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: ItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductResponse) {
            binding.textViewItemName.text = product.name
            binding.textViewSalePrice.text = "Rs ${product.saleprice}"
            binding.textViewPurchasePrice.text = "Rs ${product.purchaceprice}"
            binding.textViewStock.text = "${product.stock}"
            binding.textViewCatagory.text = product.categoryId.name
            binding.textViewSubCatagory.text = product.subcategoryId.name

            binding.textViewStock.setTextColor(
                if (product.stock > 0)
                    binding.root.context.getColor(R.color.green_color)
                else
                    binding.root.context.getColor(R.color.red_color)
            )

            binding.moreicon.setOnClickListener {
                showPopup(product)
            }
        }

        private fun showPopup(product: ProductResponse) {
            val context = binding.root.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_options, null)
            val dialog = android.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.btnUpdate).setOnClickListener {
                dialog.dismiss()
                listener?.onUpdateClicked(product._id)
            }

            dialogView.findViewById<TextView>(R.id.btnDelete).setOnClickListener {
                dialog.dismiss()
                listener?.onDeleteClicked(product._id)
            }

            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
