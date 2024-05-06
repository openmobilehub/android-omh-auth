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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.openmobilehub.android.auth.core.async.CancellableCollector
import com.openmobilehub.android.auth.sample.R
import com.openmobilehub.android.auth.sample.databinding.FragmentLoggedInBinding
import com.openmobilehub.android.auth.sample.di.AuthClientProvider
import com.openmobilehub.android.auth.sample.di.LoginState
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
        lifecycleScope.launch(Dispatchers.IO) {
            authClientProvider.getClient(requireContext()).initialize().addOnSuccess {
                setupUI()
            }.execute()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupUI() {
        binding?.run {
            btnGetUser.setOnClickListener { getUser() }
            btnRefresh.setOnClickListener { refreshToken() }
            btnRevoke.setOnClickListener { revokeToken() }
            btnLogout.setOnClickListener { logout() }
        }

        getToken()
        getUser()
    }

    private fun getToken() = lifecycleScope.launch(Dispatchers.IO) {
        val authClient = authClientProvider.getClient(requireContext())

        try {
            val token = authClient.getCredentials().accessToken

            withContext(Dispatchers.Main) {
                binding?.tvToken?.text = getString(R.string.token_placeholder, token)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showErrorDialog(e)
            }
        }
    }

    private fun getUser() = lifecycleScope.launch(Dispatchers.IO) {
        val authClient = authClientProvider.getClient(requireContext())

        val cancellable = authClient.getUser()
            .addOnSuccess { profile ->
                binding?.run {
                    val profilePicture = profile.profileImage
                        ?: "https://www.btklsby.go.id/images/placeholder/avatar.png"

                    Picasso.get().load(profilePicture).into(binding?.tvAvatar)
                    tvName.text = getString(R.string.name_placeholder, profile.name)
                    tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
                    tvEmail.text = getString(R.string.email_placeholder, profile.email)
                }

                Toast.makeText(activity, "User Data Fetched", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailure(::showErrorDialog)
            .execute()

        cancellableCollector.addCancellable(cancellable)
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val authClient = authClientProvider.getClient(requireContext())

        authClient.getCredentials().refreshAccessToken().addOnSuccess { token ->
            binding?.tvToken?.text = getString(R.string.token_placeholder, token)

            Toast.makeText(activity, "Auth Token Refreshed", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailure { e ->
            showErrorDialog(e)
        }.execute()
    }

    private fun revokeToken() = lifecycleScope.launch(Dispatchers.IO) {
        val authClient = authClientProvider.getClient(requireContext())

        val cancellable = authClient.revokeToken()
            .addOnSuccess {
                Toast.makeText(activity, "Auth Token Revoked", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailure(::showErrorDialog)
            .execute()

        cancellableCollector.addCancellable(cancellable)
    }

    private fun logout() = lifecycleScope.launch(Dispatchers.IO) {
        val authClient = authClientProvider.getClient(requireContext())

        val cancellable = authClient.signOut()
            .addOnSuccess {
                navigateToLogin()

                lifecycleScope.launch(Dispatchers.IO) {
                    LoginState(requireContext()).loggedOut()
                }

                Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailure(::showErrorDialog)
            .execute()

        cancellableCollector.addCancellable(cancellable)
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


    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_logged_in_fragment_to_login_fragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellableCollector.clear()
    }
}