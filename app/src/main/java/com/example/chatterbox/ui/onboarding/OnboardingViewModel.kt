package com.example.chatterbox.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.chatterbox.utils.SingleLiveEvent

class OnboardingViewModel : ViewModel() {
    val events = SingleLiveEvent<OnboardingViewEvents>()

    fun navigateToLogin() {
        events.postValue(OnboardingViewEvents.NavigateToLogin)
    }

    fun navigateToRegister() {
        events.postValue(OnboardingViewEvents.NavigateToRegister)
    }

}