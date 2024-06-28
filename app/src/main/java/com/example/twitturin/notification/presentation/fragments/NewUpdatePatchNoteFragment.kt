package com.example.twitturin.notification.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentNewUpdatePatchNoteBinding
import io.noties.markwon.Markwon


class NewUpdatePatchNoteFragment : Fragment() {

    private val binding by lazy { FragmentNewUpdatePatchNoteBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            patchNoteToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            Markwon.create(requireContext()).setMarkdown(patchNoteTv, "### UI/UX Improvements\n" +
                    "\n" +
                    "* Home screen bottom bar now has title\n" +
                    "* Create post Button shape changed\n" +
                    "* Detail page, Reply Edit text reworked. Now it's flexible under size of user text\n" +
                    "* Create post page rebuilded \n" +
                    "\n" +
                    "### Optimization\n" +
                    "\n" +
                    "* Mostly done code optimization so user cannot see that but there is noticeable improvements in performance\n" +
                    "\n" +
                    "### Features\n" +
                    "\n" +
                    "* Feedback system implemented. Now user can leave a feedback!\n" +
                    "* Report system implemented. Now user can report a post!\n" +
                    "* Policy of creating post added (template only!) Will be modified soon.\n" +
                    "* Search function implemented.\n" +
                    "\n" +
                    "### Bug Fixes\n" +
                    "\n" +
                    "* Fixed list of navigation bugs\n" +
                    "* Fixed search logic. Now when user search another user will be shown correct result\n" +
                    "* Fixed bug when user tries to edit his post from profile page but context of post is wrong")
        }
    }
}