package com.gasparaiciukas.owntrainer.utils.network

import android.os.Parcelable
import com.gasparaiciukas.owntrainer.model.FoodItem
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    @field:Json(name = "fdcId")
    var fdcId: Int? = null,

    @field:Json(name = "description")
    var description: String? = null,

    @field:Json(name = "lowercaseDescription")
    var lowercaseDescription: String? = null,

    @field:Json(name = "dataType")
    var dataType: String? = null,

    @field:Json(name = "gtinUpc")
    var gtinUpc: String? = null,

    @field:Json(name = "publishedDate")
    var publishedDate: String? = null,

    @field:Json(name = "brandOwner")
    var brandOwner: String? = null,

    @field:Json(name = "brandName")
    var brandName: String? = null,

    @field:Json(name = "ingredients")
    var ingredients: String? = null,

    @field:Json(name = "marketCountry")
    var marketCountry: String? = null,

    @field:Json(name = "foodCategory")
    var foodCategory: String? = null,

    @field:Json(name = "allHighlightFields")
    var allHighlightFields: String? = null,

    @field:Json(name = "score")
    var score: Double? = null,

    @field:Json(name = "foodNutrients")
    var foodNutrients: List<FoodNutrient>? = null,
) : Parcelable {
    fun toFoodItem() = FoodItem(
        title = description ?: "",
        calories = foodNutrients?.firstOrNull { it.nutrientId == 1008 }?.value ?: 0.0,
        protein = foodNutrients?.firstOrNull { it.nutrientId == 1003 }?.value ?: 0.0,
        fat = foodNutrients?.firstOrNull { it.nutrientId == 1004 }?.value ?: 0.0,
        carbs = foodNutrients?.firstOrNull { it.nutrientId == 1005 }?.value ?: 0.0,
    )
}
