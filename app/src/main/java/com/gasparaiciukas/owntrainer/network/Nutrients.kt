package com.gasparaiciukas.owntrainer.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Nutrients
(
@SerializedName("ENERC_KCAL") @Expose val calories: Double = 0.0,
@SerializedName("CHOCDF") @Expose val carbs: Double = 0.0,
@SerializedName("FAT") @Expose val fat: Double = 0.0,
@SerializedName("PROCNT") @Expose val protein: Double = 0.0
) : Parcelable