package com.example.ezinvoice.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.R
import com.example.ezinvoice.models.Invoice
import com.example.ezinvoice.models.InvoiceDisplay

class InvoiceAdapter(private var invoiceList: List<InvoiceDisplay>) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInvoiceNumber: TextView = itemView.findViewById(R.id.tvinvoiceid)
        val tvInvoiceDate: TextView = itemView.findViewById(R.id.tvdate)
        val tvInvoiceAmount: TextView = itemView.findViewById(R.id.tvprice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_invoice_layout, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoiceList[position]
        holder.tvInvoiceNumber.text = "Invoice #${invoice._id.takeLast(5).uppercase()}"
        holder.tvInvoiceDate.text = "Date: ${invoice.date.substring(0, 10)}"
        holder.tvInvoiceAmount.text = "Amount: Rs ${invoice.totalAmount}"
    }

    override fun getItemCount(): Int = invoiceList.size

    fun updateData(newList: List<InvoiceDisplay>) {
        invoiceList = newList
        notifyDataSetChanged()
    }
}
