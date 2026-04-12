package com.example.ezinvoice.apis

import com.example.ezinvoice.models.AddProductRequest
import com.example.ezinvoice.models.ProductModel
import com.example.ezinvoice.models.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface productapi {
    @Headers("Content-Type: application/json")
    @POST("products/create-product")
    suspend fun addProduct(@Body product: AddProductRequest): Response<ProductResponse>


    @GET("products/get-products-by-business/{businessId}")
    suspend fun getProductsByBusiness(@Path("businessId") businessId: String): Response<List<ProductResponse>>

    @GET("products/get-product-by-barcode/{barcode}")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String,  @Query("businessId") businessId: String): Response<ProductResponse>

    @GET("products/search-products")
    suspend fun searchProducts(
        @Query("businessId") businessId: String,
        @Query("query") query: String
    ): Response<List<ProductResponse>>


    @DELETE("products/delete-product/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Response<Void>



}
