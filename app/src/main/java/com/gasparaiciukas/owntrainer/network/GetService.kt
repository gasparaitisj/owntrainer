package com.gasparaiciukas.owntrainer.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetService {
    @Headers("Content-Type: application/json")
    @GET("search")
    fun getResponse(@Query("api_key") apiKey: String,
                    @Query("query") query: String)
    : Call<GetResponse>

    companion object {
        const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/"
    }
}