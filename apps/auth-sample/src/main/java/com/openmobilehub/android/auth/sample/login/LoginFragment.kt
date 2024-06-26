/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.sample.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.dropbox.DropboxAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import com.openmobilehub.android.auth.plugin.microsoft.MicrosoftAuthClient
import com.openmobilehub.android.auth.sample.R
import com.openmobilehub.android.auth.sample.databinding.FragmentLoginBinding
import com.openmobilehub.android.auth.sample.di.LoginState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        handleLoginResult("google")
    )
    private val facebookLoginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        handleLoginResult("facebook")
    )
    private val microsoftLoginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        handleLoginResult("microsoft")
    )

    private val dropboxLoginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        handleLoginResult("dropbox")
    )

    private var binding: FragmentLoginBinding? = null

    @Inject
    lateinit var googleAuthClient: OmhAuthClient

    @Inject
    lateinit var facebookAuthClient: FacebookAuthClient

    @Inject
    lateinit var microsoftAuthClient: MicrosoftAuthClient

    @Inject
    lateinit var dropboxAuthClient: DropboxAuthClient

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
        binding?.btnGoogleLogin?.setOnClickListener { startGoogleLogin() }
        binding?.btnFacebookLogin?.setOnClickListener { startFacebookLogin() }
        binding?.btnMicrosoftLogin?.setOnClickListener { startMicrosoftLogin() }
        binding?.btnDropboxLogin?.setOnClickListener { startDropboxLogin() }
    }

    private fun startGoogleLogin() {
        val loginIntent = googleAuthClient.getLoginIntent()
        loginLauncher.launch(loginIntent)
    }

    private fun startFacebookLogin() {
        val loginIntent = facebookAuthClient.getLoginIntent()
        facebookLoginLauncher.launch(loginIntent)
    }

    private fun startMicrosoftLogin() {
        microsoftAuthClient.initialize().addOnSuccess {
            val loginIntent = microsoftAuthClient.getLoginIntent()
            microsoftLoginLauncher.launch(loginIntent)
        }.addOnFailure { exception ->
            Log.d("LoginFragment", "Microsoft failed to initialize")
            Log.d("LoginFragment", exception.message ?: "No message")
        }.execute()
    }

    private fun startDropboxLogin() {
        val loginIntent = dropboxAuthClient.getLoginIntent()
        dropboxLoginLauncher.launch(loginIntent)
    }

    private fun navigateToLoggedIn() {
        findNavController().navigate(R.id.action_login_fragment_to_logged_in_fragment)
    }

    private fun handleLoginResult(provider: String): (ActivityResult) -> Unit {
        return { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch(Dispatchers.IO) {
                    LoginState(requireContext()).loggedIn(provider)
                    withContext(Dispatchers.Main) {
                        navigateToLoggedIn()
                    }
                }
            }

            if (result.resultCode == Activity.RESULT_CANCELED) {
                val errorMessage = result.data?.getStringExtra("errorMessage")
                handleException(Exception(errorMessage))
            }
        }
    }

    private fun handleException(exception: Exception) {
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