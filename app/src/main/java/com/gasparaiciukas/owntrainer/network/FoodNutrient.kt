package com.gasparaiciukas.owntrainer.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodNutrient(
    @field:Json(name = "nutrientId")
    var nutrientId: Int? = null,

    @field:Json(name = "nutrientName")
    var nutrientName: String? = null,

    @field:Json(name = "nutrientNumber")
    var nutrientNumber: String? = null,

    @field:Json(name = "unitName")
    var unitName: String? = null,

    @field:Json(name = "derivationCode")
    var derivationCode: String? = null,

    @field:Json(name = "derivationDescription")
    var derivationDescription: String? = null,

    @field:Json(name = "value")
    var value: Double? = null
) : Parcelable
