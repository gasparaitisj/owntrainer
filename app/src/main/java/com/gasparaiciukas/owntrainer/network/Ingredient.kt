package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Ingredient
(
@SerializedName("quantity") @Expose var quantity: Int = 0,
@SerializedName("measureURI") @Expose var measureURI: String? = null,
@SerializedName("foodId") @Expose var foodId: String? = null
)