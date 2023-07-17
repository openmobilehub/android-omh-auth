/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
