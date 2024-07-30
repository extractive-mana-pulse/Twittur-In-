package com.example.twitturin.core.extensions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.twitturin.R

fun AppCompatActivity.bottomNavigationUI(view: View) {
    val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }

    val fragmentsToHideBottomNav = setOf(
        R.id.detailFragment,
        R.id.signInFragment,
        R.id.studentRegistrationFragment,
        R.id.professorRegistrationFragment,
        R.id.editProfileFragment,
        R.id.kindFragment,
        R.id.profileFragment,
        R.id.followersListFragment,
        R.id.followingListFragment,
        R.id.publicPostFragment,
        R.id.fullScreenImageFragment,
        R.id.reportFragment,
        R.id.editTweetFragment,
        R.id.stayInFragment,
        R.id.noInternetFragment,
        R.id.feedbackFragment,
        R.id.publicPostPolicyFragment,
        R.id.newUpdatePatchNoteFragment,
        R.id.listOfLikesFragment,
        R.id.observeProfileFragment,
        R.id.settingsFragment,
        R.id.moreSettingsDetailFragment,
        R.id.shareProfileBottomSheetFragment,

        )

    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.id in fragmentsToHideBottomNav) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}