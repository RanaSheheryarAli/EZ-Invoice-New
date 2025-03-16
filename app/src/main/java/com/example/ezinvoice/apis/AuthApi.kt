package com.example.ezinvoice.apis

import com.example.ezinvoice.models.AppUser
import com.example.ezinvoice.models.LoginModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("Content-Type: application/json") // Ensure JSON format
    @POST("signup")
    fun signup(@Body user: AppUser): Call<ResponseBody>
    @POST("signin")
    fun signin(@Body user: LoginModel): Call<ResponseBody>
}