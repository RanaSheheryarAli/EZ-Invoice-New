package com.example.ezinvoice.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.R
import com.example.ezinvoice.models.SubCategoryResponse

class SubCategoryAdapter(
    private val onSelect: (SubCategoryResponse) -> Unit
) : RecyclerView.Adapter<SubCategoryAdapter.VH>() {

    private var items: List<SubCategoryResponse> = emptyList()
    private var selectedPos = RecyclerView.NO_POSITION

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name  = v.findViewById<TextView>(R.id.category_name)
        val box   = v.findViewById<CheckBox>(R.id.category_checkbox)
        init {
            v.setOnClickListener {
                val prev = selectedPos
                selectedPos = adapterPosition
                notifyItemChanged(prev)
                notifyItemChanged(selectedPos)
                onSelect(items[selectedPos])
            }
            box.setOnClickListener { v.performClick() }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.ecvcategoryitems, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val sc = items[pos]
        h.name.text = sc.name
        h.box.isChecked = (pos == selectedPos)
    }

    override fun getItemCount() = items.size

    fun setSubCategories(newList: List<SubCategoryResponse>) {
        val diff = object: DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newList.size
            override fun areItemsTheSame(o: Int, n: Int) = items[o]._id == newList[n]._id
            override fun areContentsTheSame(o: Int, n: Int) = items[o] == newList[n]
        }
        val dr = DiffUtil.calculateDiff(diff)
        items = newList
        selectedPos = RecyclerView.NO_POSITION
        dr.dispatchUpdatesTo(this)
    }
}
