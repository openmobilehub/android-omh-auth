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

package com.omh.android.auth.nongms.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.omh.android.auth.nongms.data.login.AuthRepositoryImpl
import com.omh.android.auth.nongms.data.user.UserRepositoryImpl
import com.omh.android.auth.nongms.domain.auth.AuthRepository
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.user.ProfileUseCase
import com.omh.android.auth.nongms.presentation.redirect.RedirectViewModel

/**
 * View model factory for the RedirectViewModel. It's required to pass the application context down
 * to the factories.
 */
@Suppress("UNCHECKED_CAST")
internal class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Get the Application object from extras
        val application = checkNotNull(extras[APPLICATION_KEY])
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(application)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        val userRepository = UserRepositoryImpl.getUserRepository(application)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)
        return RedirectViewModel(authUseCase, profileUseCase) as T
    }
}
