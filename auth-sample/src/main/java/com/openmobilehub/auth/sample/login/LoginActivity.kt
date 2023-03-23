package com.openmobilehub.auth.sample.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.openmobilehub.auth.sample.loggedin.LoggedInActivity
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.sample.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                navigateToLoggedIn()
            }
        }

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var omhAuthClient: OmhAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener { startLogin() }

        if (omhAuthClient.getUser(this) != null) {
            navigateToLoggedIn()
        }
    }

    private fun startLogin() {
        val loginIntent = omhAuthClient.getLoginIntent()
        loginLauncher.launch(loginIntent)
    }

    private fun navigateToLoggedIn() {
        val intent = Intent(this, LoggedInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}