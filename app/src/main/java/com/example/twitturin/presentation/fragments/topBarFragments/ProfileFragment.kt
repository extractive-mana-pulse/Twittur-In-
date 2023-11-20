package com.example.twitturin.presentation.fragments.topBarFragments

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
import com.example.twitturin.presentation.adapters.ProfileViewPagerAdapter
import com.example.twitturin.presentation.api.RetrofitInstance
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.data.users.UsersItem
import com.example.twitturin.presentation.mvvm.MainViewModel
import com.example.twitturin.presentation.mvvm.Repository
import com.example.twitturin.presentation.mvvm.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragment = this

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()

        viewModel.usersItemLiveData.observe(viewLifecycleOwner) { usersItem ->
            binding.idUser.text = usersItem?.id
            binding.profileName.text = usersItem?.fullName
            binding.customName.text = usersItem?.username
            binding.profileDescription.text = usersItem?.token
        }

        if (token != null) {
            viewModel.setDataToTextView(token)
        } else {
            Toast.makeText(requireContext(), "token error", Toast.LENGTH_SHORT).show()
        }

//        setDataToTextView()

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
                            editor.apply()
                            sessionManager.clearToken()
                            findNavController().navigate(R.id.signInFragment)
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
                        val user = binding.idUser
                        RetrofitInstance.api.deleteUser(user.id, token.toString())
                        Toast.makeText(requireContext(), "???", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.popup_menu)

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

//    private fun setDataToTextView(){
//        // TODO { rebuild it. use @header to get correct user name and full name! }
//        val sessionManager = SessionManager(requireContext())
//        val token = sessionManager.getToken()
//        if (token != null) {
//            Toast.makeText(requireContext(), token.toString(), Toast.LENGTH_SHORT).show()
//            val retrofitData = RetrofitInstance.api.getAuthUserData(token)
//            retrofitData.enqueue(object : Callback<List<UsersItem>?> {
//
//                override fun onResponse(call: Call<List<UsersItem>?>, response: Response<List<UsersItem>?>) {
//                    val responseBody = response.body()
//                    for(myData in responseBody) {
//                        binding.idUser.text = myData.id
//                        binding.profileName.text = myData.fullName
//                        binding.customName.text = myData.username
//                        binding.profileDescription.text = myData.token
//                    }
//                }
//
//                override fun onFailure(call: Call<List<UsersItem>?>, t: Throwable) {
//                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }

    fun goBack(){
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun share(){
//         val intent = Intent(Intent.ACTION_SEND)
//         intent.putExtra(Intent.EXTRA_TEXT, data.web_url)
//         intent.type = "text/plain"
//         requireContext().startActivity(Intent.createChooser(intent,"Choose app:"))
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}