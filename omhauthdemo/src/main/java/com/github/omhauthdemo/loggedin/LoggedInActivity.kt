package com.github.omhauthdemo.loggedin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.omhauthdemo.R
import com.github.omhauthdemo.databinding.ActivityLoggedInBinding
import com.github.omhauthdemo.login.LoginActivity
import com.github.openmobilehub.auth.OmhAuthClient
import com.google.android.material.snackbar.Snackbar
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
            navigateToLogin()
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

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = credentials.refreshAccessToken { e ->
            Toast.makeText(
                applicationContext,
                "Couldn't refresh token: ${e.cause}",
                Toast.LENGTH_LONG
            ).show()
        }

        if (newToken != null) {
            binding.tvToken.text = getString(R.string.token_placeholder, newToken)
        }
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}