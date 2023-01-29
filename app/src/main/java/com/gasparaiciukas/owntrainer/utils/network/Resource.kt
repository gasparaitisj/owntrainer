package com.gasparaiciukas.owntrainer.utils.network

import androidx.annotation.StringRes

data class Resource<out T>(val status: Status, val data: T?, val message: String?, @StringRes val messageRes: Int?) {
    companion object {
        fun <T> success(data: T? = null): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> error(msg: String? = null, @StringRes msgRes: Int? = null, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg, msgRes)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null, null)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
