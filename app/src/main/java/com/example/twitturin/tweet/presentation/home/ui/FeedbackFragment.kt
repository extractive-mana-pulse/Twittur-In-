package com.example.twitturin.tweet.presentation.home.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    private val binding by lazy { FragmentFeedbackBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            feedbackBackBtn.setOnClickListener { findNavController().navigateUp() }

            feedbackSendEmailBtn.setOnClickListener {
                val subject = feedbackSpinner.selectedItem.toString()
                val message = messageFeedbackEt.text.toString().trim()

                sendEmail(subject, message)
            }

            ArrayAdapter.createFromResource(requireContext(), R.array.feedback_array, android.R.layout.simple_spinner_item).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                feedbackSpinner.adapter = adapter
            }

            feedbackSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    if (parent?.getItemAtPosition(position).toString() == "Other"){
                        feedbackTopicLayout2.visibility = View.VISIBLE
                    } else {
                        feedbackTopicLayout2.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {  }
            }
        }
    }

    private fun sendEmail(subject: String, message: String) {
        /**ACTION_SENDTO action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SENDTO)
        /**To send an email you need to specify mailto: as URI using setData() method\
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("invoker1441@gmail.com"))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }
}