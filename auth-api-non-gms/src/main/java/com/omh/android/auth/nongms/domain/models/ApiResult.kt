package com.omh.android.auth.nongms.domain.models

internal sealed class ApiResult<out T> {

    data class Success<out R>(val data: R) : ApiResult<R>()

    sealed class Error : ApiResult<Nothing>() {

        data class ApiError(val code: Int, val body: String) : Error()

        data class RuntimeError(val exception: Throwable) : Error()

        data class NetworkError(val exception: Throwable) : Error()
    }

    inline fun <R> map(mapFunction: (T) -> R): ApiResult<R> {
        return when (this) {
            is Success -> {
                val transformedData = mapFunction(data)
                Success(transformedData)
            }
            is Error -> this
        }
    }
}
