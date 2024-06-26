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

package com.openmobilehub.android.auth.sample.base

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.openmobilehub.android.auth.sample.R
import com.openmobilehub.android.auth.sample.databinding.ActivityMainBinding
import com.openmobilehub.android.auth.sample.di.AuthClientProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var authClientProvider: AuthClientProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupEdgeToEdgeInsets()
        setupNavigation()
    }

    private fun setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.statusbarPlaceholder.updateLayoutParams { height = systemBars.top }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(
            /* id = */ R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        setupGraph()
        setupToolbar()
    }

    private fun setupGraph() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                authClientProvider.getClient(applicationContext)

                withContext(Dispatchers.Main) {
                    navGraph.setStartDestination(R.id.logged_in_fragment)
                    navController.graph = navGraph
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    navGraph.setStartDestination(R.id.login_fragment)
                    navController.graph = navGraph
                }
            }
        }
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.logged_in_fragment, R.id.login_fragment)
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}