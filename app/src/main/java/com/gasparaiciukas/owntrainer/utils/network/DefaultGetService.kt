package com.gasparaiciukas.owntrainer.utils.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DefaultGetService : GetService {
    @Headers("Content-Type: application/json")
    @GET("search")
    override suspend fun getFoods(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("dataType") dataType: String?,
        @Query("numberOfResultsPerPage") numberOfResultsPerPage: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("sortBy") sortBy: String?,
        @Query("sortOrder") sortOrder: String?,
        @Query("requireAllWords") requireAllWords: Boolean?,
    ): Response<GetResponse>
}
