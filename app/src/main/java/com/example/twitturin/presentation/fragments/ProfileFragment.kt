package com.example.twitturin.presentation.fragments

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
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentProfileBinding
import com.example.twitturin.presentation.adapters.ViewPagedAdapter
import com.example.twitturin.presentation.api.RetrofitInstance
import com.example.twitturin.presentation.data.users.ApiUsersItem
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragment = this

        setDataToTextView()

//        binding.logout.setOnClickListener {
//
//        }

        binding.threeDotMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                    R.id.edit_profile -> {
                        Toast.makeText(requireContext(), "navigate to edit profile page", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(Intent.ACTION_SEND)
//                        intent.putExtra(Intent.EXTRA_TEXT, data.web_url)
//                        intent.type = "text/plain"
//                        holder.itemView.context.startActivity(Intent.createChooser(intent,"Choose app:"))
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
                            findNavController().navigate(R.id.signInFragment)
                        }
                        alertDialogBuilder.setNegativeButton("No") { _, _ ->
                            requireActivity().finish()
                        }
                        alertDialogBuilder.setCancelable(false)
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
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


        val adapter = ViewPagedAdapter(childFragmentManager, lifecycle)
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

    private fun setDataToTextView(){
        val retrofitData = RetrofitInstance.api.getUserData()

        retrofitData.enqueue(object : Callback<List<ApiUsersItem>?> {
            override fun onResponse(call: Call<List<ApiUsersItem>?>, response: Response<List<ApiUsersItem>?>) {
                val responseBody = response.body()!!

                for(myData in responseBody) {

                    if (binding.customName.text.equals("")){
                        binding.customName.visibility = View.GONE
                        binding.profileDescription.visibility = View.GONE
                    }

                    binding.profileName.text = myData.username
                    /* username is like full name. full name is some name probably associated with nickname i dunno */
                    binding.customName.text = myData.fullName
                }
            }

            override fun onFailure(call: Call<List<ApiUsersItem>?>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun goBack(){
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}