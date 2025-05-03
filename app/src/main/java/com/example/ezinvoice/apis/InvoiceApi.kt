package com.example.ezinvoice.apis

import com.example.ezinvoice.models.CustomerRequest
import com.example.ezinvoice.models.CustomerResponse
import com.example.ezinvoice.models.InvoiceCountResponse
import com.example.ezinvoice.models.InvoiceDisplay
import com.example.ezinvoice.models.InvoiceRequest
import com.example.ezinvoice.models.InvoiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InvoiceApi {
    @POST("customers/find-or-create")
    suspend fun findOrCreateCustomer(@Body customer: CustomerRequest): Response<CustomerResponse>

    @POST("invoices/create-invoice")
    suspend fun createInvoice(@Body invoice: InvoiceRequest): Response<InvoiceResponse>

    @GET("invoices/count-today/{businessId}")
    suspend fun getTodayInvoiceCount(@Path("businessId") businessId: String): Response<InvoiceCountResponse>

    @GET("invoices/all/{businessId}")
    suspend fun getInvoicesByBusinessId(@Path("businessId") businessId: String): Response<List<InvoiceDisplay>>



}
