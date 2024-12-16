package com.example.ezinvoice.adaptors
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.R
import com.example.ezinvoice.models.Show_Report_Items

class ReportAdatperItems(private val itemtList: List<Show_Report_Items>):RecyclerView.Adapter<ReportAdatperItems.ReportViewHolder>() {
    class ReportViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.tv_client_name)
        val invoicesCount: TextView = itemView.findViewById(R.id.tv_invoices_count)
        val invoiceAmount: TextView = itemView.findViewById(R.id.tv_invoice_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_items_layout, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int = itemtList.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val reportItem = itemtList[position]
        holder.clientName.text = reportItem.itemsName
        holder.invoicesCount.text = "Invoices: ${reportItem.itemsCount}"
        holder.invoiceAmount.text = reportItem.itemsAmount
    }
}