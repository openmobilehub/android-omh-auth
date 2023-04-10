package com.omh.android.auth.nongms.domain.models

internal sealed class ApiResult<out T> {

    data class Success<out R>(val data: R) : ApiResult<R>()

    sealed class Error : ApiResult<Nothing>() {

        data class ApiError(val code: Int, val body: String) : Error()

        data class RuntimeError(val exception: Throwable) : Error()

        data class NetworkError(val exception: Throwable) : Error()
    }

    /**
     * Maps a result of type [T] to another type [R] with error catching, returning a
     * [Error.RuntimeError] to represent any exceptions.
     */
    @SuppressWarnings("TooGenericExceptionCaught")
    inline fun <R> map(mapFunction: (T) -> R): ApiResult<R> {
        return when (this) {
            is Success -> {
                try {
                    val transformedData = mapFunction(data)
                    Success(transformedData)
                } catch (e: RuntimeException) {
                    Error.RuntimeError(e)
                }
            }
            is Error -> this
        }
    }
}
