package com.omh.android.auth.nongms.presentation.redirect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.omh.android.auth.nongms.databinding.ActivityRedirectBinding
import com.omh.android.auth.nongms.factories.ViewModelFactory
import com.omh.android.auth.nongms.utils.EventWrapper
import com.omh.android.auth.nongms.utils.lifecycle.LifecycleUtil
import com.omh.android.auth.nongms.utils.nullOrHandled

internal class RedirectActivity : AppCompatActivity() {

    private val viewModel: RedirectViewModel by viewModels { ViewModelFactory() }

    private val binding: ActivityRedirectBinding by lazy {
        ActivityRedirectBinding.inflate(LayoutInflater.from(this))
    }

    private var caughtRedirect = false

    private val tabsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleCustomTabsClosed()
        }

    private fun handleCustomTabsClosed() {
        LifecycleUtil.runOnResume(lifecycle = lifecycle, owner = this) {
            if (!caughtRedirect) returnResult(Activity.RESULT_CANCELED)
        }
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
        customTabsIntent.intent.data = uri
        tabsLauncher.launch(customTabsIntent.intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        caughtRedirect = true
        val data: Uri? = intent?.data
        val authCode = data?.getQueryParameter("code")
        val error = data?.getQueryParameter("error code")
        if (authCode == null) {
            Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            returnResult(Activity.RESULT_CANCELED)
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
