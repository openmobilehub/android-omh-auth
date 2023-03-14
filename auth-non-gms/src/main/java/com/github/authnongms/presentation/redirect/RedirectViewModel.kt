package com.github.authnongms.presentation.redirect

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.authnongms.domain.auth.AuthUseCase
import com.github.authnongms.domain.user.ProfileUseCase
import com.github.authnongms.utils.EventWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

internal class RedirectViewModel(
    private val authUseCase: AuthUseCase,
    private val profileUseCase: ProfileUseCase
) : ViewModel() {

    private val _tokenResponseEvent = MutableLiveData<EventWrapper<Boolean>>()
    val tokenResponseEvent: LiveData<EventWrapper<Boolean>> = _tokenResponseEvent

    fun getLoginUrl(scopes: String, packageName: String): Uri {
        return authUseCase.getLoginUrl(scopes, packageName).toUri()
    }

    fun requestTokens(
        authCode: String,
        packageName: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        authUseCase.requestTokens(authCode, packageName)
            .catch { exception ->
                // Handle exceptions
                Log.e(RedirectViewModel::class.java.name, "$exception", )
                _tokenResponseEvent.postValue(EventWrapper(false))
            }
            .collect { tokens ->
                val clientId = checkNotNull(authUseCase.clientId)
                profileUseCase.resolveIdToken(tokens.idToken, clientId)
                _tokenResponseEvent.postValue(EventWrapper(true))
            }
    }

    fun setClientId(clientId: String) {
        authUseCase.clientId = clientId
    }
}
