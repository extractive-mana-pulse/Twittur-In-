package com.example.twitturin.core.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

fun Fragment.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    /** read mode about flowWithLifecycle here @see https://habr.com/ru/companies/otus/articles/564050/ */
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.currentStateFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { block() }
    }
}