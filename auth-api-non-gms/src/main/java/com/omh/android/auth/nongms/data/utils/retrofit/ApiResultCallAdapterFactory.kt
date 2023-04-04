package com.omh.android.auth.nongms.data.utils.retrofit

import com.omh.android.auth.nongms.domain.models.ApiResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

internal class ApiResultCallAdapterFactory : CallAdapter.Factory() {

    @SuppressWarnings("ReturnCount")
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) { "Return type must be a parameterized type." }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResult::class.java) return null
        check(responseType is ParameterizedType) { "Response type must be a parameterized type." }

        val resultType = getParameterUpperBound(0, responseType)
        return ApiResultCallAdapter<Any>(resultType)
    }
}
