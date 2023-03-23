package com.openmobilehub.auth.sample.loggedin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.openmobilehub.auth.sample.login.LoginActivity
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.sample.R
import com.openmobilehub.auth.sample.databinding.ActivityLoggedInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoggedInActivity : AppCompatActivity() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient
    private val credentials by lazy { omhAuthClient.getCredentials() }

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

    private fun logout() {
        omhAuthClient.signOut()
        navigateToLogin()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = credentials.blockingRefreshToken()

        if (newToken != null) {
            withContext(Dispatchers.Main) {
                binding.tvToken.text = getString(R.string.token_placeholder, newToken)
            }
        }
    }

    private fun showException(message: String) = lifecycleScope.launch(Dispatchers.Main) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}