package com.gasparaiciukas.owntrainer.utils.network

import com.gasparaiciukas.owntrainer.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetService {
    @Headers("Content-Type: application/json")
    @GET("search")
    suspend fun getFoods(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("query") query: String,
        @Query("dataType") dataType: String? = null,
        @Query("numberOfResultsPerPage") numberOfResultsPerPage: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("requireAllWords") requireAllWords: Boolean? = null,
    ): Response<GetResponse>
}
