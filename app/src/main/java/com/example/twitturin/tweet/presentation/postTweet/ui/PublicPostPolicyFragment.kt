package com.example.twitturin.tweet.presentation.postTweet.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostPolicyBinding
import com.example.twitturin.tweet.presentation.postTweet.util.Constants
import io.noties.markwon.Markwon

class PublicPostPolicyFragment : Fragment() {

    private val binding by lazy { FragmentPublicPostPolicyBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            policyToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            policyAgreeBtn.setOnClickListener { findNavController().navigate(R.id.action_publicPostPolicyFragment_to_publicPostFragment) }

                Markwon.create(requireContext()).setMarkdown(policyContextTv, "## At TwitturIn, we are committed to protecting the privacy and security of our users. This Privacy Policy explains how we collect, use, and protect your personal information when you use our chat application.\n" +
                    "\n" +
                    "###  Information We Collect\n" +
                    "\n" +

                    " * **User Account Information**: When you create an account with us, we may collect your username, email address, and other profile information you provide.\n" +
                    "\n * **Chat Messages**: The content of the messages you send and receive through our chat app is stored temporarily to enable the chat functionality. We do not access or read the content of your messages.\n" +
                    "\n * **Device Information**: We may collect information about the device you use to access our app, such as the device model, operating system, and IP address.\n" +
                    "###  How We Use Your Information\n" +
                    "\n" +
                    "\n * **Account Management**: We use your account information to create and maintain your user profile, enable the chat functionality, and provide customer support.\n" +
                    "\n * **Improving Our Services**: We may use the information we collect to analyze usage patterns and trends, and to improve the features and performance of our chat app.\n" +
                    "\n * **Legal Compliance**: We may use or disclose your information if we are required to do so by law or in response to valid requests by public authorities.\n" +
                    "###Data Retention and Security\n" +
                    "\n" +
                    "\n * **Message Retention**: We temporarily store the content of your chat messages to enable the chat functionality. The messages are automatically deleted after [X] days.\n" +
                    "\n * **Data Security**: We implement appropriate technical and organizational measures to protect your personal information from unauthorized access, disclosure, or misuse.\n" +
                    "\n ### Your Rights and Choices\n" +
                    "\n" +
                    "\n * **Access and Correction**: You have the right to access, update, and correct the personal information we have about you.\n" +
                    "\n * **Deletion**: You can request the deletion of your account and personal information by contacting our support team.\n" +
                    "\n * **Opt-Out**: You can opt-out of receiving certain communications from us, such as marketing or promotional messages, by adjusting your account settings or by following the unsubscribe instructions in the communication.\n" +
                    "\n ### Changes to this Privacy Policy\n" +
                    "We may update this Privacy Policy from time to time to reflect changes in our practices or applicable laws. We will notify you of any material changes by posting the updated policy on our app or website.\n" +
                    "\n" +
                    "Contact Us\n" +
                    "If you have any questions or concerns about our privacy practices, please contact us at [Gmail](https://mail.google.com/mail/u/0/#inbox?compose=new)" +
                    "\n" +
                    "### Chat Etiquette and Guidelines\n" +
                    "\n" +
                    "\n 1. **Respect and Inclusivity**:\n" +
                    "Refrain from using discriminatory, hateful, or derogatory language based on race, ethnicity, gender, sexual orientation, religion, or any other protected characteristic.\n" +
                    "Be respectful and considerate of others, regardless of their background or beliefs.\n" +
                    "\n 2. **Appropriate Content**:\n" +
                    "Do not engage in or encourage illegal activities.\n" +
                    "Avoid posting explicit, violent, or sexually suggestive content that may be inappropriate or offensive to other users.\n" +
                    "Do not share private or confidential information about yourself or others without permission.\n" +
                    "\n 3. **Constructive Communication**:\n" +
                    "Express yourself clearly and avoid misunderstandings by being mindful of your tone and word choice.\n" +
                    "Engage in productive discussions and provide constructive feedback, rather than resorting to personal attacks or flame wars.\n" +
                    "If you encounter disagreements or conflicts, try to resolve them respectfully and avoid escalating the situation.\n" +
                    "\n 4. **Privacy and Security**:\n" +
                    "Do not share your own or others' personal information, such as phone numbers, addresses, or financial details, without consent.\n" +
                    "Be cautious about clicking on links or attachments from unknown sources, as they may contain malware or inappropriate content.\n" +
                    "\n 5. **Moderation and Reporting**:\n" +
                    "If you encounter any content or behavior that violates these guidelines, report it to the chat app's moderation team or administrators.\n" +
                    "Cooperate with the app's moderators and administrators if they need to investigate or address any issues.\n" +
                    "\n 6. **Responsible Use**:\n" +
                    "Use the chat app in a manner that does not disrupt or interfere with others' ability to engage in the platform.\n" +
                    "Avoid spamming, flooding, or engaging in any other behavior that may be considered abusive or disruptive.")
        }
    }
}