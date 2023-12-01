package com.example.twitturin.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.twitturin.model.repo.Repository

class ViewModelFactory2(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(repository) as T
    }
}