package com.example.twitturin.ui.fragments.topBarFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.model.api.Api
import com.example.twitturin.model.api.RetrofitInstance
import com.example.twitturin.model.data.tweets.ApiTweetsItem
import com.example.twitturin.model.data.users.UsersItem
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.ProfileViewPagerAdapter
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.viewModels.ProfileViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val strBuilder = StringBuilder()

//    private val sessionManager = SessionManager(requireContext())

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragment = this

        val sessionManager = SessionManager(requireContext())
        setDataToTextView()

        binding.threeDotMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                    R.id.edit_profile -> {
                        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                        true
                    }
                    R.id.logout -> {
                        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
                        alertDialogBuilder.setTitle("Logout")
                        alertDialogBuilder.setMessage("Are you sure you want to log out?")
                        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                            val pref = requireActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.remove("remember")
                            editor.clear()
                            editor.apply()
                            sessionManager.clearToken()
                            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                        }
                        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialogBuilder.setCancelable(false)
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        true
                    }
                    R.id.delete_account -> {
                        deleteUser()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.popup_menu_profile)

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception){
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        }

        val adapter = ProfileViewPagerAdapter(childFragmentManager, lifecycle)
        binding.vp2.adapter = adapter
        TabLayoutMediator(binding.tb, binding.vp2) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Tweets"
                }
                1 -> {
                    tab.text = "Likes"
                }
            }
        }.attach()
    }

    private fun setDataToTextView() {
        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()

        val call = RetrofitInstance.api.getAuthUserData("Bearer $token")

        call.enqueue(object : Callback<List<UsersItem>> {
            override fun onResponse(call: Call<List<UsersItem>>, response: Response<List<UsersItem>>) {
                if (response.isSuccessful) {

                    val users = response.body()
                    if (users != null) {
                        for (user in users){
                            binding.profileName.text = user.fullName
                            binding.customName.text = user.username
                            strBuilder.append(user.id)
                        }
                        binding.userIdTv.text  = strBuilder
                    }
                } else {
                    Toast.makeText(requireContext(), response.code(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<UsersItem>>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteUser() {
        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        binding.userIdTv.text = strBuilder

        viewModel.deleteUser(strBuilder.toString(),"Bearer $token")

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {

                is DeleteResult.Success -> {
                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                    Toast.makeText(requireContext(), "deleted ?", Toast.LENGTH_SHORT).show()
                }
                is DeleteResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun goBack(){
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

//    private fun share(){
//         val intent = Intent(Intent.ACTION_SEND)
//         intent.putExtra(Intent.EXTRA_TEXT, data.web_url)
//         intent.type = "text/plain"
//         requireContext().startActivity(Intent.createChooser(intent,"Choose app:"))
//    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}