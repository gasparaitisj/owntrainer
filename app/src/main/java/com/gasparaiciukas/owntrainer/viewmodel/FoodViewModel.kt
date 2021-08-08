package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.APIConstants
import com.gasparaiciukas.owntrainer.network.FoodApi
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.GetService
import com.gasparaiciukas.owntrainer.network.Hint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class FoodViewModel : ViewModel() {
    private lateinit var getResponse: GetResponse
    private lateinit var getService: GetService
    private val _foodsApi = MutableLiveData<List<FoodApi>>()
    val foodsApi: LiveData<List<FoodApi>>
        get() = _foodsApi

    init {
        buildGetRequest()
    }

    private fun buildGetRequest() {
        val retrofitGet = Retrofit.Builder()
            .baseUrl(GetService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        getService = retrofitGet.create(GetService::class.java)
    }

    fun sendGet(query: String) {
        val newFoodsApi = arrayListOf<FoodApi>()
        val call = getService.getResponse(APIConstants.APP_ID, APIConstants.APP_KEY, query)
        call.enqueue(object : Callback<GetResponse> {
            override fun onResponse(call: Call<GetResponse>, response: Response<GetResponse>) {
                getResponse = response.body()!!
                val hints: List<Hint> = getResponse.hints ?: listOf()
                for (i in hints.indices) {
                    hints[i].foodApi?.let { newFoodsApi.add(it) }
                }
                _foodsApi.value = newFoodsApi
                Timber.d("Sent: %s | Received: %s",
                    getResponse.text,
                    newFoodsApi.getOrNull(0)?.label ?: "No results found!"
                )
            }
            override fun onFailure(call: Call<GetResponse?>, t: Throwable) {
                Timber.d(t)
            }
        })
    }
}