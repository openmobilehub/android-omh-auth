package com.github.omhauthdemo.loggedin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.omhauthdemo.R
import com.github.omhauthdemo.databinding.ActivityLoggedInBinding
import com.github.omhauthdemo.login.LoginActivity
import com.github.openmobilehub.auth.api.OmhAuthClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoggedInActivity : AppCompatActivity() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient
    private val credentials by lazy { omhAuthClient.getCredentials(this) }

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

        val profile = requireNotNull(omhAuthClient.getUser(this))
        binding.tvEmail.text = getString(R.string.email_placeholder, profile.email)
        binding.tvName.text = getString(R.string.name_placeholder, profile.name)
        binding.tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
        binding.tvToken.text = getString(R.string.token_placeholder, credentials.accessToken)
    }

    private fun logout() = lifecycleScope.launch(Dispatchers.IO) {
        credentials.logout { e ->
            launch(Dispatchers.Main) { showRevokeException("Couldn't revoke token: ${e.message}") }
        }
        navigateToLogin()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = credentials.refreshAccessToken { e ->
            showRevokeException("Couldn't refresh token: ${e.message}")
            logout()
        }

        if (newToken != null) {
            binding.tvToken.text = getString(R.string.token_placeholder, newToken)
        }
    }

    private fun showRevokeException(message: String) = lifecycleScope.launch(Dispatchers.Main) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}