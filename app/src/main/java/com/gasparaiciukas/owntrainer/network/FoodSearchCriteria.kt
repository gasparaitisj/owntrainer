package com.gasparaiciukas.owntrainer.network

import com.squareup.moshi.Json

data class FoodSearchCriteria(
    @field:Json(name = "query")
    var query: String? = null,

    @field:Json(name = "generalSearchInput")
    var generalSearchInput: String? = null,

    @field:Json(name = "pageNumber")
    var pageNumber: Int? = null,

    @field:Json(name = "numberOfResultsPerPage")
    var numberOfResultsPerPage: Int? = null,

    @field:Json(name = "pageSize")
    var pageSize: Int? = null,

    @field:Json(name = "requireAllWords")
    var requireAllWords: Boolean? = null
)
