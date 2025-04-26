package com.example.ezinvoice.apis

import com.example.ezinvoice.models.CatagoryModel
import com.example.ezinvoice.models.CatagoryResponce
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface addcatageoryapi {

    @Headers("Content-Type: application/json") // Ensure JSON format
    @POST("categories/add-catagory")
    suspend fun addcategory(@Body category: CatagoryModel): Response<CatagoryResponce>

    @GET("categories/getall-catagory/{businessId}") // ✅ Fix: Use @GET with Path parameter
    suspend  fun getAllCategory(@retrofit2.http.Path("businessId") businessId: String?): Response<List<CatagoryResponce>>
}