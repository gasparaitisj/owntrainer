package com.gasparaiciukas.owntrainer.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodApi
(
@SerializedName("foodId") @Expose var foodId: String,
@SerializedName("uri") @Expose var uri: String?,
@SerializedName("label") @Expose var label: String,
@SerializedName("nutrients") @Expose var nutrients: Nutrients,
@SerializedName("category") @Expose var category: String?,
@SerializedName("categoryLabel") @Expose var categoryLabel: String?,
@SerializedName("image") @Expose var image: String?,
var position: Int = 0
) : Parcelable