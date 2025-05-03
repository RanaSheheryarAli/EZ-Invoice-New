package com.example.ezinvoice.apis

import com.example.ezinvoice.models.AppUser
import com.example.ezinvoice.models.BusinessInfo
import com.example.ezinvoice.models.BusinessResponse
import com.example.ezinvoice.models.LoginModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface BusinessinfoApi {
    @Headers("Content-Type: application/json") // Ensure JSON format
    @POST("businesses/add-business")
   suspend fun createbusiness(@Body user: BusinessInfo): Response<BusinessInfo>

    @GET("businesses/get-business/{userId}")
    suspend fun getbusiness(@retrofit2.http.Path("userId") userId: String?): Response<BusinessInfo>


}