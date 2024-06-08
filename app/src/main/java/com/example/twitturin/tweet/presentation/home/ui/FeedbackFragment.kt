package com.example.twitturin.tweet.presentation.home.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
                val subject = topicFeedbackEt.text.toString().trim()
                val message = messageFeedbackEt.text.toString().trim()

                sendEmail(subject, message)
            }
        }
    }

    private fun sendEmail(subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method\
        and data type will be to text/plain using setType() method*/
//        mIntent.setDataAndType(Uri.parse("mailto:"), "text/plain")
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "message/rfc822"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("invoker1441@gmail.com"))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }
}