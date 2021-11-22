package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.BuildConfig
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.GetService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class FoodViewModel : ViewModel() {
    private lateinit var getResponse: GetResponse
    private lateinit var getService: GetService
    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>>
        get() = _foods

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
        val call = getService.getResponse(BuildConfig.API_KEY, query)
        call.enqueue(object : Callback<GetResponse> {
            override fun onResponse(call: Call<GetResponse>, response: Response<GetResponse>) {
                getResponse = response.body()!!
                Timber.i("API Response: ")
                val foodsFromResponse = getResponse.foods
                if (foodsFromResponse != null) {
                    _foods.value = foodsFromResponse
                    for (food in foodsFromResponse) {
                        Timber.i("Description: " + food.description + " | " + food.score)
                    }
                }
            }
            override fun onFailure(call: Call<GetResponse?>, t: Throwable) {
                Timber.d(t)
            }
        })
    }
}