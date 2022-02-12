package com.gasparaiciukas.owntrainer.network

import com.gasparaiciukas.owntrainer.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetService {
    @Headers("Content-Type: application/json")
    @GET("search")
    suspend fun getFoods(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("query") query: String,
        @Query("query") dataType: String = "Foundation,SR Legacy"
    ): GetResponse

    companion object {
        private const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/"

        fun create(): GetService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetService::class.java)
    }
}