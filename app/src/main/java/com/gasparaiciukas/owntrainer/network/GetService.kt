package com.gasparaiciukas.owntrainer.network

import com.gasparaiciukas.owntrainer.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetService {
    @Headers("Content-Type: application/json")
    @GET("search")
    fun getResponse(@Query("api_key") apiKey: String = BuildConfig.API_KEY,
                    @Query("query") query: String,
                    @Query("query") dataType: String = "Foundation,SR Legacy,Branded"
    )
    : Call<GetResponse>

    companion object {
        const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/"
    }
}