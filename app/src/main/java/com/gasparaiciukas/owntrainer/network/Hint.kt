package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Hint
(
@SerializedName("food") @Expose var foodApi: FoodApi? = null
)