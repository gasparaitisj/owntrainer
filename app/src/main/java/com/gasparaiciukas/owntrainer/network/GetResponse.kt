package com.gasparaiciukas.owntrainer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetResponse
(
@SerializedName("text") @Expose var text: String? = null,
@SerializedName("hints") @Expose var hints: List<Hint>? = null
)