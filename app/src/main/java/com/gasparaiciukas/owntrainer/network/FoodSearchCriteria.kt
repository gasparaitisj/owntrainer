package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FoodSearchCriteria
    (
    @SerializedName("query") @Expose var query: String? = null,
    @SerializedName("generalSearchInput") @Expose var generalSearchInput: String? = null,
    @SerializedName("pageNumber") @Expose var pageNumber: Int? = null,
    @SerializedName("numberOfResultsPerPage") @Expose var numberOfResultsPerPage: Int? = null,
    @SerializedName("pageSize") @Expose var pageSize: Int? = null,
    @SerializedName("requireAllWords") @Expose var requireAllWords: Boolean? = null
)
