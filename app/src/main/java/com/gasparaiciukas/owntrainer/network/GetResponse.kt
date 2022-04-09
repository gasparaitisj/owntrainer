package com.gasparaiciukas.owntrainer.network

import com.squareup.moshi.Json

class GetResponse(
    @field:Json(name = "totalHits")
    var totalHits: Int,

    @field:Json(name = "currentPage")
    var currentPage: Int,

    @field:Json(name = "totalPages")
    var totalPages: Int,

    @field:Json(name = "pageList")
    var pageList: List<Int>? = null,

    @field:Json(name = "foodSearchCriteria")
    var foodSearchCriteria: FoodSearchCriteria? = null,

    @field:Json(name = "foods")
    var foods: MutableList<Food>? = null
)