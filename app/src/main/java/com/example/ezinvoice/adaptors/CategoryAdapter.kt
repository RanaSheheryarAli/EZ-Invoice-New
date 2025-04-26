package com.example.ezinvoice.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.R
import com.example.ezinvoice.models.CatagoryResponce

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categoryList: List<CatagoryResponce> = listOf()

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.category_name)
        val checkBox: CheckBox = view.findViewById(R.id.category1)  // Ensure this ID matches the XML layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ecvcategoryitems, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name// Directly access `name` instead of `category.name`
        holder.checkBox.isChecked = false // Default state
    }

    override fun getItemCount(): Int = categoryList.size

    fun setCategories(newCategories: List<CatagoryResponce>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = categoryList.size
            override fun getNewListSize() = newCategories.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return categoryList[oldItemPosition]._id == newCategories[newItemPosition]._id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return categoryList[oldItemPosition] == newCategories[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        categoryList = newCategories
        diffResult.dispatchUpdatesTo(this)
    }
}
