package com.example.twitturin.notification.presentation.fragments

import android.content.Intent
import android.net.Uri
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

            patchNotPageUpdateBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://objects.githubusercontent.com/github-production-release-asset-2e65be/733415969/eef51851-4613-4b6b-813e-baaf079092c5?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=releaseassetproduction%2F20240627%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240627T121633Z&X-Amz-Expires=300&X-Amz-Signature=0b843602fa4653288cadf96bf9501a5be77e4f2f0d32aa2102cfc76f568f3281&X-Amz-SignedHeaders=host&actor_id=0&key_id=0&repo_id=733415969&response-content-disposition=attachment%3B%20filename%3Dapp-debug.apk&response-content-type=application%2Fvnd.android.package-archive")
                startActivity(intent)
            }

            patchNoteToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            Markwon.create(requireContext()).setMarkdown(patchNoteTv, "### UI/UX Improvements\n" +
                    "\n" +
                    "* Home screen bottom bar now has title\n" +
                    "* Create post Button shape changed\n" +
                    "* Detail page, Reply Edit text reworked. Now it's flexible under size of user text\n" +
                    "* Create post page rebuild \n" +
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