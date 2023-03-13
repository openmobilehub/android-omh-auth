package com.github.authnongms.domain.models

class DataResponse<T>(
    val response: T? = null,
    val errorDetail: String? = null
) {
    val isSuccessful: Boolean
        get() = response != null
}
