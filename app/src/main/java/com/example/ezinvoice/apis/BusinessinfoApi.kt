package com.example.ezinvoice.apis

import com.example.ezinvoice.models.AppUser
import com.example.ezinvoice.models.BusinessInfo
import com.example.ezinvoice.models.LoginModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface BusinessinfoApi {
    @Headers("Content-Type: application/json") // Ensure JSON format
    @POST("add-business")
    fun createbusiness(@Body user: BusinessInfo): Call<ResponseBody>


    @GET("get-business/{userId}")
    fun getbusiness(@retrofit2.http.Path("userId") userId: String?): Call<BusinessInfo>

}