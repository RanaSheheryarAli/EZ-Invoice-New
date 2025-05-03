package com.example.ezinvoice.apis

import com.example.ezinvoice.models.Show_Report_Clients
import com.example.ezinvoice.models.Show_Report_Items
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReportApi {
    @GET("invoices/stats/{businessId}")
    suspend fun getTotalStats(@Path("businessId") businessId: String): Response<Map<String, Any>>

    @GET("invoices/sales-graph/{businessId}")
    suspend fun getSalesGraph(@Path("businessId") businessId: String): Response<Map<String, Double>>

    @GET("invoices/top-products/{businessId}")
    suspend fun getTopSellingItems(@Path("businessId") businessId: String): Response<List<Show_Report_Items>>

    @GET("customers/top-customers/{businessId}")
    suspend fun getTopClients(@Path("businessId") businessId: String): Response<List<Show_Report_Clients>>
}
