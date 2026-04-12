package com.example.ezinvoice.apis

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadImgApi {
    @Multipart
    @POST("businesses/upload-logo")
  suspend  fun uploadBusinessLogo(@Part logo: MultipartBody.Part): Response<ResponseBody>

}