package com.gasparaiciukas.owntrainer.network

import retrofit2.Response

interface GetService {
    suspend fun getFoods(): Response<GetResponse>
}
