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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.nongms.databinding.ActivityRedirectBinding
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import com.omh.android.auth.nongms.factories.ViewModelFactory
import com.omh.android.auth.nongms.utils.Constants
import com.omh.android.auth.nongms.utils.EventWrapper
import com.omh.android.auth.nongms.utils.lifecycle.LifecycleUtil
import com.omh.android.auth.nongms.utils.nullOrHandled

internal class RedirectActivity : AppCompatActivity() {

    private val viewModel: RedirectViewModel by viewModels { ViewModelFactory() }

    private val binding: ActivityRedirectBinding by lazy {
        ActivityRedirectBinding.inflate(LayoutInflater.from(this))
    }

    private var caughtRedirect = false
    private var clientId: String = ""

    private val tabsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleCustomTabsClosed()
        }

    private fun handleCustomTabsClosed() {
        LifecycleUtil.runOnResume(lifecycle = lifecycle, owner = this) {
            if (!caughtRedirect) {
                returnResult(
                    result = Activity.RESULT_CANCELED,
                    exception = OmhAuthException.LoginCanceledException()
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (intent.getStringExtra(CLIENT_ID) == null) {
            returnResult(
                result = Activity.RESULT_CANCELED,
                exception = OmhAuthException.RecoverableLoginException(OmhAuthStatusCodes.DEVELOPER_ERROR)
            )
            return
        }
        clientId = intent.getStringExtra(CLIENT_ID)!!
        openCustomTabLogin()

        viewModel.tokenResponseEvent.observe(this, this::observeTokenResponse)
    }

    private fun observeTokenResponse(eventWrapper: EventWrapper<ApiResult<OAuthTokens>>?) {
        if (eventWrapper.nullOrHandled()) return
        when (val result: ApiResult<OAuthTokens>? = eventWrapper.getContentIfHandled()) {
            is ApiResult.Success -> {
                returnResult(Activity.RESULT_OK)
            }

            is ApiResult.Error.NetworkError -> {
                returnResult(
                    Activity.RESULT_CANCELED,
                    OmhAuthException.RecoverableLoginException(
                        OmhAuthStatusCodes.NETWORK_ERROR,
                        result.cause
                    )
                )
            }

            else -> {
                returnResult(
                    Activity.RESULT_CANCELED,
                    OmhAuthException.UnrecoverableLoginException()
                )
            }
        }
    }

    private fun openCustomTabLogin() {
        val scopes = intent.getStringExtra(SCOPES)
        if (scopes.isNullOrEmpty() || packageName.isNullOrEmpty()) {
            returnResult(
                Activity.RESULT_CANCELED,
                OmhAuthException.RecoverableLoginException(OmhAuthStatusCodes.DEVELOPER_ERROR)
            )
            return
        }
        val uri = viewModel.getLoginUrl(scopes, packageName, clientId)

        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.intent.data = uri
        tabsLauncher.launch(customTabsIntent.intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        caughtRedirect = true
        val data: Uri? = intent?.data
        val authCode = data?.getQueryParameter("code")
        val error = data?.getQueryParameter("error")
        if (authCode == null) {
            handleLoginError(error)
            return
        }
        viewModel.requestTokens(authCode, packageName, clientId)
    }

    private fun handleLoginError(error: String?) {
        val code = when (error) {
            ACCESS_DENIED_RESPONSE -> OmhAuthStatusCodes.ACCESS_DENIED
            else -> OmhAuthStatusCodes.DEFAULT_ERROR
        }
        returnResult(
            result = RESULT_CANCELED,
            exception = OmhAuthException.RecoverableLoginException(code)
        )
    }

    private fun returnResult(result: Int, exception: OmhAuthException? = null) {
        val intent = Intent()
        if (result == Activity.RESULT_CANCELED) {
            intent.putExtra(Constants.CAUSE_KEY, exception)
        }
        setResult(result, intent)
        finish()
    }

    companion object {
        const val SCOPES = "scopes"
        const val CLIENT_ID = "client_id"
        const val ACCESS_DENIED_RESPONSE = "access_denied"
    }
}
