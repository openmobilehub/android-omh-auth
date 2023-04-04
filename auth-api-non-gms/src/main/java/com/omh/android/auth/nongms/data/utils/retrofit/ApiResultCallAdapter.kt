package com.omh.android.auth.nongms.data.utils.retrofit

import com.omh.android.auth.nongms.domain.models.ApiResult
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter

internal class ApiResultCallAdapter<R>(private val successType: Type) : CallAdapter<R, Call<ApiResult<R>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Call<ApiResult<R>> {
        return ApiResultCall(call, successType)
    }
}
