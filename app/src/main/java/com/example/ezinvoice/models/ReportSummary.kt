package com.example.ezinvoice.models

data class ReportSummary(
    val totalInvoices: Int,
    val totalSales: Double,
    val totalExpense: Double,
    val profitOrLoss: Double,
    val salesGraph: List<SalesGraphEntry>
)

data class SalesGraphEntry(
    val date: String,
    val amount: Double
)
