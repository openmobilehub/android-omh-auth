package com.omh.android.auth.nongms.domain.models

sealed class ApiResult<out T> {

    data class Success<out R>(val data: R) : ApiResult<R>()

    data class Error(val exception: String) : ApiResult<Nothing>()
}
