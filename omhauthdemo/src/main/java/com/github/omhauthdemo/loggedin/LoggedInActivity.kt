package com.github.omhauthdemo.loggedin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.omhauthdemo.databinding.ActivityLoggedInBinding
import com.github.omhauthdemo.login.LoginActivity

class LoggedInActivity : AppCompatActivity() {

    private val loginLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                navigateToLogin()
            }
        }

    private val binding: ActivityLoggedInBinding by lazy {
        ActivityLoggedInBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            navigateToLogin()
            // TODO replace with
            // loginLauncher.launch(OmhAuthClient.getLoginIntent())
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}