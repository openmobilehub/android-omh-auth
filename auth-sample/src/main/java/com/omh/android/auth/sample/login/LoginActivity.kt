package com.omh.android.auth.sample.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.sample.databinding.ActivityLoginBinding
import com.omh.android.auth.sample.loggedin.LoggedInActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                omhAuthClient.getAccountFromIntent(result.data)
                navigateToLoggedIn()
            } catch (exception: OmhAuthException) {
                val errorMessage = OmhAuthStatusCodes.getStatusCodeString(exception.statusCode)
                AlertDialog.Builder(this)
                    .setTitle("An error has occurred.")
                    .setMessage(errorMessage)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
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

        if (omhAuthClient.getUser() != null) {
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