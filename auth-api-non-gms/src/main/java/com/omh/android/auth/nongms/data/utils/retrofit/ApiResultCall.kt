package com.omh.android.auth.nongms.data.utils.retrofit

import com.omh.android.auth.nongms.domain.models.ApiResult
import java.io.IOException
import java.lang.reflect.Type
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

internal class ApiResultCall<T>(
    private val delegate: Call<T>,
    private val successType: Type
) : Call<ApiResult<T>> {

    override fun enqueue(callback: Callback<ApiResult<T>>) {
        delegate.enqueue(ApiResultCallback(callback))
    }

    override fun clone(): Call<ApiResult<T>> {
        @Suppress("UNCHECKED_CAST")
        return delegate.clone() as Call<ApiResult<T>>
    }

    override fun execute(): Response<ApiResult<T>> {
        @Suppress("UNCHECKED_CAST")
        return delegate.execute() as Response<ApiResult<T>>
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    inner class ApiResultCallback(private val callback: Callback<ApiResult<T>>) : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val apiResponse: Response<ApiResult<T>> = Response.success(response.toApiResult())
            callback.onResponse(this@ApiResultCall, apiResponse)
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
            val apiResult = when (throwable) {
                is IOException -> ApiResult.Error.NetworkError(throwable)
                else -> ApiResult.Error.RuntimeError(throwable)
            }
            callback.onResponse(this@ApiResultCall, Response.success(apiResult))
        }

        private fun Response<T>.toApiResult(): ApiResult<T> = when {
            // Http error response (4xx - 5xx)
            !isSuccessful -> {
                ApiResult.Error.ApiError(HttpException(this))
            }
            // Http success response with body
            body() != null -> {
                ApiResult.Success(body()!!)
            }
            // if we defined Unit as success type it means we expected no response body
            // e.g. in case of 204 No Content
            successType == Unit::class.java -> {
                @Suppress("UNCHECKED_CAST")
                ApiResult.Success(Unit) as ApiResult<T>
            }
            else -> {
                val exception = UnknownError("Response body was null")
                ApiResult.Error.RuntimeError(exception)
            }
        }
    }
}
