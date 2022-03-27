package com.gasparaiciukas.owntrainer.network

import com.gasparaiciukas.owntrainer.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DefaultGetService : GetService {
    @Headers("Content-Type: application/json")
    @GET("search")
    suspend fun getFoods(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("query") query: String,
        @Query("query") dataType: String = "Foundation,SR Legacy"
    ): Response<GetResponse>
}