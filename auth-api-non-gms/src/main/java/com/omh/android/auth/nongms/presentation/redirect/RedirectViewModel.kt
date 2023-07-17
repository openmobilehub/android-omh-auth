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

package com.omh.android.auth.nongms.presentation.redirect

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import com.omh.android.auth.nongms.domain.user.ProfileUseCase
import com.omh.android.auth.nongms.utils.EventWrapper
import kotlinx.coroutines.launch

internal class RedirectViewModel(
    private val authUseCase: AuthUseCase,
    private val profileUseCase: ProfileUseCase
) : ViewModel() {

    private val _tokenResponseEvent = MutableLiveData<EventWrapper<ApiResult<OAuthTokens>>>()
    val tokenResponseEvent: LiveData<EventWrapper<ApiResult<OAuthTokens>>> = _tokenResponseEvent

    fun getLoginUrl(scopes: String, packageName: String, clientId: String): Uri {
        return authUseCase.getLoginUrl(scopes, packageName, clientId).toUri()
    }

    fun requestTokens(
        authCode: String,
        packageName: String,
        clientId: String,
    ) = viewModelScope.launch {
        var apiResult = authUseCase.requestTokens(authCode, packageName, clientId)
        if (apiResult is ApiResult.Success) {
            val tokens: OAuthTokens = apiResult.data
            try {
                profileUseCase.resolveIdToken(tokens.idToken, clientId)
            } catch (omhException: OmhAuthException) {
                apiResult = ApiResult.Error.RuntimeError(omhException)
            }
        }
        _tokenResponseEvent.postValue(EventWrapper(apiResult))
    }
}
