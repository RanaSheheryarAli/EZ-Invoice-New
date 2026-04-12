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

class CategoryAdapter(
    private val onSelect: (CatagoryResponce) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categoryList: List<CatagoryResponce> = emptyList()
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.category_name)
        val checkBox: CheckBox    = view.findViewById(R.id.category_checkbox) // matches your XML

        init {
            view.setOnClickListener {
                // use adapterPosition instead of bindingAdapterPosition
                val previous = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onSelect(categoryList[selectedPosition])
            }
            checkBox.setOnClickListener { view.performClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // inflate the exact XML you showed: ecvcategoryitems.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ecvcategoryitems, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name
        holder.checkBox.isChecked = (position == selectedPosition)
    }

    override fun getItemCount(): Int = categoryList.size

    fun setCategories(newCategories: List<CatagoryResponce>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = categoryList.size
            override fun getNewListSize() = newCategories.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                categoryList[oldPos]._id == newCategories[newPos]._id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                categoryList[oldPos] == newCategories[newPos]
        }
        val result = DiffUtil.calculateDiff(diff)
        categoryList = newCategories
        selectedPosition = RecyclerView.NO_POSITION
        result.dispatchUpdatesTo(this)
    }

    fun getSelected(): CatagoryResponce? =
        categoryList.getOrNull(selectedPosition)
}
