package com.gasparaiciukas.owntrainer.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodNutrient
(
    @SerializedName("nutrientId") @Expose var nutrientId: Int? = null,
    @SerializedName("nutrientName") @Expose var nutrientName: String? = null,
    @SerializedName("nutrientNumber") @Expose var nutrientNumber: String? = null,
    @SerializedName("unitName") @Expose var unitName: String? = null,
    @SerializedName("derivationCode") @Expose var derivationCode: String? = null,
    @SerializedName("derivationDescription") @Expose var derivationDescription: String? = null,
    @SerializedName("value") @Expose var value: Double? = null
) : Parcelable
