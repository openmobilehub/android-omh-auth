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

package com.openmobilehub.android.auth.sample.loggedin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.CancellableCollector
import com.openmobilehub.android.auth.sample.R
import com.openmobilehub.android.auth.sample.databinding.FragmentLoggedInBinding
import com.openmobilehub.android.auth.sample.di.AuthClientProvider
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LoggedInFragment : Fragment() {

    @Inject
    lateinit var authClientProvider: AuthClientProvider

    private var binding: FragmentLoggedInBinding? = null

    private val cancellableCollector = CancellableCollector()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoggedInBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        getToken()
    }

    private fun setupUI() {
        binding?.run {
            btnGetUser.setOnClickListener { getUser() }
            btnRefresh.setOnClickListener { refreshToken() }
            btnRevoke.setOnClickListener { revokeToken() }
            btnLogout.setOnClickListener { logout() }
        }

        getUser()
    }

    private fun getUser() {
        authClientProvider.getClient().getUser()
            .addOnSuccess { profile ->
                binding?.run {
                    Picasso.get().load(profile.profileImage).into(binding?.tvAvatar)
                    tvName.text = getString(R.string.name_placeholder, profile.name)
                    tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
                    tvEmail.text = getString(R.string.email_placeholder, profile.email)
                }

                Toast.makeText(activity, "Fetched User Data", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailure(::showErrorDialog)
            .execute()
    }

    private fun revokeToken() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cancellable = authClientProvider.getClient().revokeToken()
                .addOnFailure(::showErrorDialog)
                .addOnSuccess {
                    Toast.makeText(activity, "Auth Token Revoked", Toast.LENGTH_SHORT)
                        .show()
                }
                .execute()
            cancellableCollector.addCancellable(cancellable)
        }
    }

    private fun getToken() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            val token = when (val credentials = authClientProvider.getClient().getCredentials()) {
                is OmhCredentials -> credentials.accessToken
                is GoogleAccountCredential -> {
                    requestGoogleToken(credentials)
                }

                null -> return@launch
                else -> error("Unsupported credential type")
            }

            withContext(Dispatchers.Main) {
                binding?.tvToken?.text = getString(R.string.token_placeholder, token)
            }
        } catch (e: NotImplementedError) {
            Log.e("LoggedInFragment", "Not implemented", e)
        }
    }

    private fun requestGoogleToken(credentials: GoogleAccountCredential): String? {
        return try {
            credentials.token
        } catch (e: UserRecoverableAuthException) {
            e.printStackTrace()
            logout()
            null
        }
    }

    private fun logout() {
        lifecycleScope.launch(Dispatchers.Main) {
            val cancellable = authClientProvider.getClient().signOut()
                .addOnSuccess { navigateToLogin() }
                .addOnFailure(::showErrorDialog)
                .execute()
            cancellableCollector.addCancellable(cancellable)
        }
    }

    private fun showErrorDialog(exception: Throwable) {
        exception.printStackTrace()
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("An error has occurred.")
            .setMessage(exception.message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            val newToken =
                when (val credentials = authClientProvider.getClient().getCredentials()) {
                    is OmhCredentials -> credentials.blockingRefreshToken()
                    is GoogleAccountCredential -> requestGoogleToken(credentials)
                    else -> error("Unsupported credential type")
                } ?: return@launch

            withContext(Dispatchers.Main) {
                binding?.tvToken?.text = getString(R.string.token_placeholder, newToken)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showErrorDialog(e)
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_logged_in_fragment_to_login_fragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellableCollector.clear()
    }
}