package com.gasparaiciukas.owntrainer.network

import com.gasparaiciukas.owntrainer.BuildConfig
import retrofit2.Response

interface GetService {
    suspend fun getFoods(
        apiKey: String = BuildConfig.API_KEY,
        query: String,
        dataType: String? = null,
        numberOfResultsPerPage: Int? = null,
        pageSize: Int? = null,
        pageNumber: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        requireAllWords: Boolean? = null,
    ): Response<GetResponse>
}
