package com.openmobilehub.auth.nongms.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.openmobilehub.auth.nongms.data.login.AuthRepositoryImpl
import com.openmobilehub.auth.nongms.data.user.UserRepositoryImpl
import com.openmobilehub.auth.nongms.domain.auth.AuthRepository
import com.openmobilehub.auth.nongms.domain.auth.AuthUseCase
import com.openmobilehub.auth.nongms.domain.user.ProfileUseCase
import com.openmobilehub.auth.nongms.presentation.redirect.RedirectViewModel

/**
 * View model factory for the RedirectViewModel. It's required to pass the application context down
 * to the factories.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

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
