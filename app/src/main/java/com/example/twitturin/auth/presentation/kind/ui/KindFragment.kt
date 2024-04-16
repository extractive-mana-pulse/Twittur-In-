package com.example.twitturin.auth.presentation.kind.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.kind.sealed.KindUiEvent
import com.example.twitturin.auth.presentation.kind.vm.KindViewModel
import com.example.twitturin.databinding.FragmentKindBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KindFragment : Fragment() {

    private val kindViewModel : KindViewModel by viewModels()
    private val binding by lazy { FragmentKindBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            kindViewModel.kindEvent.observe(viewLifecycleOwner){ event ->
                when(event) {
                    is KindUiEvent.NavigateToProfReg -> {
                        findNavController().navigate(R.id.action_kindFragment_to_professorRegistrationFragment)
                    }
                    is KindUiEvent.NavigateToStudReg -> {
                        findNavController().navigate(R.id.action_kindFragment_to_studentRegistrationFragment)
                    }
                    is KindUiEvent.OnBackPressedFromKindPage -> {
                        findNavController().popBackStack()
                    }
                }
            }

            professorKindBtn.setOnClickListener { kindViewModel.sendKindEvents(KindUiEvent.NavigateToProfReg) }

            studentKindBtn.setOnClickListener { kindViewModel.sendKindEvents(KindUiEvent.NavigateToStudReg) }

            backBtnKind.setOnClickListener { kindViewModel.sendKindEvents(KindUiEvent.OnBackPressedFromKindPage) }
        }
    }
}