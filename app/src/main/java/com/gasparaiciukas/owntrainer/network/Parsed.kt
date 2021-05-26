package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Parsed
(
@SerializedName("quantity") @Expose var quantity: Int = 0,
@SerializedName("measure") @Expose var measure: String? = null,
@SerializedName("food") @Expose var food: String? = null,
@SerializedName("foodId") @Expose var foodId: String? = null,
@SerializedName("weight") @Expose var weight: Int = 0,
@SerializedName("retainedWeight") @Expose var retainedWeight: Int = 0,
@SerializedName("measureURI") @Expose var measureURI: String? = null,
@SerializedName("status") @Expose var status: String? = null
)