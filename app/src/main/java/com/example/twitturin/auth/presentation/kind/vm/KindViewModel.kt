package com.example.twitturin.auth.presentation.kind.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.presentation.kind.sealed.KindUiEvent
import com.example.twitturin.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KindViewModel @Inject constructor(): ViewModel() {

    private val _kindEvent = SingleLiveEvent<KindUiEvent>()
    val kindEvent: LiveData<KindUiEvent> = _kindEvent

    private fun onBackPressedKind(){
        _kindEvent.value = KindUiEvent.OnBackPressedFromKindPage
    }

    private fun onProfPressed(){
        _kindEvent.value = KindUiEvent.NavigateToProfReg
    }

    private fun onStudPressed(){
        _kindEvent.value = KindUiEvent.NavigateToStudReg
    }

    fun sendKindEvents(event: KindUiEvent) {
        when(event){
            is KindUiEvent.NavigateToStudReg -> { onStudPressed()}
            is KindUiEvent.NavigateToProfReg -> { onProfPressed()}
            is KindUiEvent.OnBackPressedFromKindPage -> { onBackPressedKind()}
        }
    }
}