package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetResponse {
    @SerializedName("totalHits")
    @Expose
    var totalHits: Int? = null

    @SerializedName("currentPage")
    @Expose
    var currentPage: Int? = null

    @SerializedName("totalPages")
    @Expose
    var totalPages: Int? = null

    @SerializedName("pageList")
    @Expose
    var pageList: List<Int>? = null

    @SerializedName("foodSearchCriteria")
    @Expose
    var foodSearchCriteria: FoodSearchCriteria? = null

    @SerializedName("foods")
    @Expose
    var foods: List<Food>? = null
}