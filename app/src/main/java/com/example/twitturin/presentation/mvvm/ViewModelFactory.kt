package com.example.twitturin.presentation.mvvm

import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}