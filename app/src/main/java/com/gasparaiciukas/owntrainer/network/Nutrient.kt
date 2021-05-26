package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Nutrient
(
@SerializedName("label") @Expose val label: String? = null,
@SerializedName("quantity") @Expose val quantity: Double = 0.0,
@SerializedName("unit") @Expose val unit: String? = null
)