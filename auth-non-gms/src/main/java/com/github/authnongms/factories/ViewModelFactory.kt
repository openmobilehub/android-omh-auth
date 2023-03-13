package com.github.authnongms.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.github.authnongms.domain.auth.LoginUseCase
import com.github.authnongms.domain.user.ProfileUseCase
import com.github.authnongms.presentation.redirect.RedirectViewModel

/**
 * View model factory for the RedirectViewModel. It's required to pass the application context down
 * to the factories.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Get the Application object from extras
        val application = checkNotNull(extras[APPLICATION_KEY])
        val loginUseCase = LoginUseCase.createLoginUseCase(application)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(application)
        return RedirectViewModel(loginUseCase, profileUseCase) as T
    }
}
