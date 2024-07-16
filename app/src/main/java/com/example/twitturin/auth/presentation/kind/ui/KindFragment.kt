package com.example.twitturin.auth.presentation.kind.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.kind.sealed.KindUiEvent
import com.example.twitturin.auth.presentation.kind.vm.KindViewModel
import com.example.twitturin.databinding.FragmentKindBinding
import kotlinx.coroutines.launch

class KindFragment : Fragment() {

    private val kindViewModel : KindViewModel by viewModels()
    private val binding by lazy { FragmentKindBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                kindViewModel.kindEventResult.collect {
                    when(it) {
                        is KindUiEvent.NavigateToProfReg -> { findNavController().navigate(R.id.action_kindFragment_to_professorRegistrationFragment) }
                        is KindUiEvent.NavigateToStudReg -> { findNavController().navigate(R.id.action_kindFragment_to_studentRegistrationFragment) }
                        is KindUiEvent.OnBackPressed -> { findNavController().navigateUp() }
                        is KindUiEvent.StateNoting -> {  }
                    }
                }
            }

            studentKindBtn.setOnClickListener { kindViewModel.onStudPressed() }
            backBtnKind.setOnClickListener { kindViewModel.onBackPressedKind() }
            professorKindBtn.setOnClickListener { kindViewModel.onProfPressed() }
        }
    }
}