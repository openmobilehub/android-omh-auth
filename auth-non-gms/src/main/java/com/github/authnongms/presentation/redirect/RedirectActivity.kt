package com.github.authnongms.presentation.redirect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.github.authnongms.factories.ViewModelFactory
import com.github.authnongms.utils.EventWrapper
import com.github.authnongms.utils.nullOrHandled
import com.github.omhauthnongms.databinding.ActivityRedirectBinding

internal class RedirectActivity : AppCompatActivity() {

    private val viewModel: RedirectViewModel by viewModels { ViewModelFactory() }

    private val binding: ActivityRedirectBinding by lazy {
        ActivityRedirectBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (intent.getStringExtra(CLIENT_ID) == null) {
            finish()
            return
        }
        val clientId: String = intent.getStringExtra(CLIENT_ID)!!
        viewModel.setClientId(clientId)
        openCustomTabLogin()

        viewModel.tokenResponseEvent.observe(this, this::observeTokenResponse)
    }

    private fun observeTokenResponse(eventWrapper: EventWrapper<Boolean>?) {
        if (eventWrapper.nullOrHandled()) return
        val result = if (eventWrapper.getContentIfHandled() == true) {
            Activity.RESULT_OK
        } else {
            Activity.RESULT_CANCELED
        }
        returnResult(result)
    }

    private fun openCustomTabLogin() {
        val scopes = intent.getStringExtra(SCOPES)
        if (scopes.isNullOrEmpty() || packageName.isNullOrEmpty()) {
            returnResult(Activity.RESULT_CANCELED)
            return
        }
        val uri = viewModel.getLoginUrl(scopes, packageName)

        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, uri)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data: Uri? = intent?.data
        val authCode = data?.getQueryParameter("code")
        val error = data?.getQueryParameter("error code")
        if (authCode == null) {
            Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            returnResult(RESULT_CANCELED)
            return
        }
        viewModel.requestTokens(authCode, packageName)
    }

    private fun returnResult(result: Int) {
        val intent = Intent()
        setResult(result, intent)
        finish()
    }

    companion object {
        internal const val SCOPES = "scopes"
        internal const val CLIENT_ID = "client_id"
    }
}
