package com.gasparaiciukas.owntrainer.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food
    (
    @SerializedName("fdcId") @Expose var fdcId: Int? = null,
    @SerializedName("description") @Expose var description: String? = null,
    @SerializedName("lowercaseDescription") @Expose var lowercaseDescription: String? = null,
    @SerializedName("dataType") @Expose var dataType: String? = null,
    @SerializedName("gtinUpc") @Expose var gtinUpc: String? = null,
    @SerializedName("publishedDate") @Expose var publishedDate: String? = null,
    @SerializedName("brandOwner") @Expose var brandOwner: String? = null,
    @SerializedName("brandName") @Expose var brandName: String? = null,
    @SerializedName("ingredients") @Expose var ingredients: String? = null,
    @SerializedName("marketCountry") @Expose var marketCountry: String? = null,
    @SerializedName("foodCategory") @Expose var foodCategory: String? = null,
    @SerializedName("allHighlightFields") @Expose var allHighlightFields: String? = null,
    @SerializedName("score") @Expose var score: Double? = null,
    @SerializedName("foodNutrients") @Expose var foodNutrients: List<FoodNutrient>? = null
) : Parcelable