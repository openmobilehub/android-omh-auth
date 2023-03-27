package com.omh.android.auth.sample.loggedin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.omh.android.auth.sample.login.LoginActivity
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.sample.R
import com.omh.android.auth.sample.databinding.ActivityLoggedInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoggedInActivity : AppCompatActivity() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient

    private val binding: ActivityLoggedInBinding by lazy {
        ActivityLoggedInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.btnRefresh.setOnClickListener {
            refreshToken()
        }

        val profile = requireNotNull(omhAuthClient.getUser())
        binding.tvEmail.text = getString(R.string.email_placeholder, profile.email)
        binding.tvName.text = getString(R.string.name_placeholder, profile.name)
        binding.tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
        getToken()
    }

    private fun getToken() = lifecycleScope.launch(Dispatchers.IO) {
        val token = when (val credentials = omhAuthClient.getCredentials()) {
            is OmhCredentials -> credentials.accessToken
            is GoogleAccountCredential -> credentials.token
            else -> error("Unsupported credential type")
        }

        withContext(Dispatchers.Main) {
            binding.tvToken.text = getString(R.string.token_placeholder, token)
        }
    }

    private fun logout() {
        omhAuthClient.signOut()
        navigateToLogin()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = when (val credentials = omhAuthClient.getCredentials()) {
            is OmhCredentials -> credentials.blockingRefreshToken()
            is GoogleAccountCredential -> credentials.token
            else -> error("Unsupported credential type")
        }

        if (newToken != null) {
            withContext(Dispatchers.Main) {
                binding.tvToken.text = getString(R.string.token_placeholder, newToken)
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}