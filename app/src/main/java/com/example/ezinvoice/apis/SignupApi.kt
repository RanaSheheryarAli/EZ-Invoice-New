package com.example.ezinvoice.apis

import com.example.ezinvoice.models.AppUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SignupApi {
    @Headers("Content-Type: application/json") // Ensure JSON format
    @POST("signup")
    fun signup(@Body user: AppUser): Call<ResponseBody>
}