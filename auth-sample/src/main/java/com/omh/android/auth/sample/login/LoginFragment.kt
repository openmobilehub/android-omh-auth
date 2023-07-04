package com.omh.android.auth.sample.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.sample.R
import com.omh.android.auth.sample.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        /* contract = */ ActivityResultContracts.StartActivityForResult(),
        /* callback = */ ::handleLoginResult
    )

    private var binding: FragmentLoginBinding? = null

    @Inject
    lateinit var omhAuthClient: OmhAuthClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnLogin?.setOnClickListener { startLogin() }
    }

    private fun startLogin() {
        val loginIntent = omhAuthClient.getLoginIntent()
        loginLauncher.launch(loginIntent)
    }

    private fun navigateToLoggedIn() {
        findNavController().navigate(R.id.action_login_fragment_to_logged_in_fragment)
    }

    private fun handleLoginResult(result: ActivityResult) {
        try {
            omhAuthClient.getAccountFromIntent(result.data)
            navigateToLoggedIn()
        } catch (exception: OmhAuthException) {
            handleException(exception)
        }
    }

    private fun handleException(exception: OmhAuthException) {
        exception.printStackTrace()
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("An error has occurred.")
            .setMessage(exception.message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}