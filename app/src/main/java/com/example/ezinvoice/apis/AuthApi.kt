package com.example.ezinvoice.apis

import com.example.ezinvoice.models.AppUser
import com.example.ezinvoice.models.LoginModel
import com.example.ezinvoice.models.LoginResponse
import com.example.ezinvoice.models.SignupResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("Content-Type: application/json") // Ensure JSON format
    @POST("auth/signup")
    suspend fun signup1(@Body user: AppUser): Response<SignupResponse>

    @POST("auth/signin")
    suspend fun signin1(@Body user: LoginModel): Response<LoginResponse>
}