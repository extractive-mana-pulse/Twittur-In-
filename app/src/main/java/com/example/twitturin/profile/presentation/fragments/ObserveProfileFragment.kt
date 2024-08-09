package com.example.twitturin.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentObserveProfileBinding
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.search.domain.model.SearchUser
import dagger.hilt.android.AndroidEntryPoint

/*
* Process: com.example.twitturin, PID: 16085 java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.hashCode()' on a null object reference
* at com.example.twitturin.search.domain.model.SearchUser.hashCode(Unknown Source:19)
*
* TODO: Such error can occur when u have not correct configured data class. Don't forget to add serialized name annotation and use parcelize or serializable to parse using Bundle.
* */

@AndroidEntryPoint
class ObserveProfileFragment : Fragment() {

    private val followViewModel by viewModels<FollowViewModel>()
    private val binding by lazy { FragmentObserveProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            val data = arguments?.getParcelable("item") as? SearchUser
            data?.apply {
                observeProfileKind.text = kind
                observeProfileUsername.text = username
                observeProfileLocationTv.text = country
                observeFollowingCounterTv.text = followingCount.toString()
                observeFollowersCounterTv.text = followersCount.toString()
                observeProfileUserAvatar.loadImagesWithGlideExt(data.profilePicture)
                observeProfileBiography.text = bio ?: resources.getString(R.string.empty_bio)
                observeProfileFullName.text = fullName ?: resources.getString(R.string.default_user_fullname)
            }

            observeFollowBtn.setOnClickListener {

                followViewModel.followUser(data?.id!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                followViewModel.follow.observe(viewLifecycleOwner) {
                    when(it) {
                        is Follow.Error -> { snackbarView.snackbarError(snackbarView, it.message, ""){} }
                        is Follow.Success -> {
                            snackbarView.snackbar(snackbarView, "you follow: ${data.username}")
                            observeFollowBtn.text = resources.getString(R.string.unfollow)
                        }
                        Follow.Loading -> {}
                    }
                }
            }

            observeProfileToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            observeProfileToolbar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.add_to_block_list -> {
                        // snackbar not shows. fix that!
                        snackbarView.snackbar(snackbarView, resources.getString(R.string.developing))
                        true
                    }
                    R.id.share_profile -> {
                        findNavController().navigate(R.id.shareProfileBottomSheetFragment)
                        true
                    }
                    else -> false
                }
            }
            // TODO: write other part of code here.
        }
    }
}