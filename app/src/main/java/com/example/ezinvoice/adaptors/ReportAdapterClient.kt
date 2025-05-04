package com.example.ezinvoice.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.R
import com.example.ezinvoice.models.Invoice
import com.example.ezinvoice.models.Show_Report_Clients

class ReportAdapterClient(private val clientList: List<Show_Report_Clients>) : RecyclerView.Adapter<ReportAdapterClient.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.tv_client_name)
        val invoicesCount: TextView = itemView.findViewById(R.id.tv_invoices_count)
        val invoiceAmount: TextView = itemView.findViewById(R.id.tv_invoice_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_items_layout, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val reportItem = clientList[position]
        holder.clientName.text = reportItem.name
        holder.invoicesCount.text = reportItem.invoiceCount.toString()
        holder.invoiceAmount.text = "Rs: ${reportItem.totalSpent}"
    }

    override fun getItemCount(): Int = clientList.size
}
