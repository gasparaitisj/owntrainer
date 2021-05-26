package com.gasparaiciukas.owntrainer.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetService {
    @Headers("Content-Type: application/json")
    @GET("parser")
    fun getResponse(@Query("app_id") appId: String,
                    @Query("app_key") appKey: String,
                    @Query("ingr") query: String)
    : Call<GetResponse>

    companion object {
        const val BASE_URL = "https://api.edamam.com/api/food-database/v2/"
    }
}