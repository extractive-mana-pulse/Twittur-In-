package com.example.twitturin.tweet.presentation.postTweet.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.tweet.presentation.postTweet.sealed.PostTweet
import com.example.twitturin.tweet.presentation.postTweet.util.Constants
import com.example.twitturin.tweet.presentation.postTweet.vm.PostTweetViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PublicPostFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    private lateinit var sharedPreferences: SharedPreferences
    private val postTweetViewModel: PostTweetViewModel by viewModels()
    private val binding by lazy { FragmentPublicPostBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    init {
        lifecycleScope.launchWhenResumed {
            sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

            val showDialog = sharedPreferences.getBoolean(Constants.SHOW_DIALOG_KEY, true)
            if (showDialog) {
                showDialog()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment = this

        val token = sessionManager.getToken()

        binding.apply {

            btnTweet.setOnClickListener {
                btnTweet.isEnabled = false
                postTweetViewModel.postTheTweet(contentEt.text.toString(), "Bearer $token")
            }

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnTweet.isEnabled = !contentEt.text.isNullOrBlank()
                }

                override fun afterTextChanged(s: Editable?) {
                    //
                }
            }

            contentEt.addTextChangedListener(textWatcher)

            postTweetViewModel.postTweetResult.observe(viewLifecycleOwner) { result ->

                when (result) {
                    is PostTweet.Success -> {
                        findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment)
                    }

                    is PostTweet.Error -> {
                        binding.publicPostRootLayout.snackbarError(
                            requireActivity().findViewById(R.id.public_post_root_layout),
                            error = result.message,
                            ""){}
                        btnTweet.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        val dialogCheckbox = dialogView.findViewById<CheckBox>(R.id.dialog_checkbox)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialog_button)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setView(dialogView)
            .create()

        dialogButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(Constants.SHOW_DIALOG_KEY, !isChecked).apply()
        }
        dialog.show()
    }
}



















//            var isPressed = false
//            val mEditor = contentEt
//            val mPreview: TextView = findViewById(R.id.preview)

//            mEditor.setEditorHeight(200);
//            mEditor.setEditorFontSize(22);
//            mEditor.setEditorFontColor(Color.RED);
//            mEditor.setEditorBackgroundColor(Color.BLUE);
            //mEditor.setBackgroundColor(Color.BLUE);
            //mEditor.setBackgroundResource(R.drawable.bg);
//            mEditor.setPadding(10, 10, 10, 10);
            //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//            mEditor.setPlaceholder("Insert text here...");

//            mEditor.setOnTextChangeListener { text ->
//                mPreview.text = text
//            }

//            actionUndo.setOnClickListener {
//                if (isPressed) {
//                    mEditor.undo()
//                    actionUndo.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.undo()
//                    actionUndo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionRedo.setOnClickListener {
//                if (isPressed){
//                    mEditor.redo()
//                    actionRedo.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.redo()
//                    actionRedo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionBold.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setBold()
//                    actionBold.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setBold()
//                    actionBold.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionItalic.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setItalic()
//                    actionItalic.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setItalic()
//                    actionItalic.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionUnderline.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setUnderline()
//                    actionUnderline.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setUnderline()
//                    actionUnderline.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }

//            actionSubscript.setOnClickListener { mEditor.setSubscript() }
//            actionSuperscript.setOnClickListener { mEditor.setSuperscript() }

//            actionStrikethrough.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setStrikeThrough()
//                    actionStrikethrough.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setStrikeThrough()
//                    actionStrikethrough.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }

            /** Leave only headlines for title and subtitle */
//            actionHeading1.setOnClickListener { mEditor.setHeading(1) }
//            actionHeading2.setOnClickListener { mEditor.setHeading(2) }
//            actionHeading3.setOnClickListener { mEditor.setHeading(3) }
//            actionHeading4.setOnClickListener { mEditor.setHeading(4) }
//            actionHeading5.setOnClickListener { mEditor.setHeading(5) }
//            actionHeading6.setOnClickListener { mEditor.setHeading(6) }

//            actionTxtColor.setOnClickListener {
//                var isChanged = false
//                mEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
//                isChanged = !isChanged
//            }

//            actionBgColor.setOnClickListener {
//                var isChanged = false
//                mEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
//                isChanged = !isChanged
//            }

//            actionIndent.setOnClickListener { mEditor.setIndent() }
//            actionOutdent.setOnClickListener { mEditor.setOutdent() }

//            actionAlignLeft.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setAlignLeft()
//                    actionAlignLeft.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setAlignLeft()
//                    actionAlignLeft.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionAlignCenter.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setAlignCenter()
//                    actionAlignCenter.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setAlignCenter()
//                    actionAlignCenter.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionAlignRight.setOnClickListener {
//                if (isPressed) {
//                    mEditor.setAlignRight()
//                    actionAlignRight.setBackgroundColor(Color.TRANSPARENT)
//                } else {
//                    mEditor.setAlignRight()
//                    actionAlignRight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
//                }
//                isPressed = !isPressed
//            }
//
//            actionInsertBullets.setOnClickListener { mEditor.setBullets() }
//            actionInsertNumbers.setOnClickListener { mEditor.setNumbers() }

//            actionBlockquote.setOnClickListener { mEditor.setBlockquote() }
//            actionInsertImage.setOnClickListener {
//                 TODO
//                Toast.makeText(this@MainActivity, "build image input", Toast.LENGTH_SHORT).show()
//            }

//            actionInsertAudio.setOnClickListener {
            // TODO
//                Toast.makeText(this@MainActivity, "build voice input", Toast.LENGTH_SHORT).show()
//            }

//            actionInsertVideo.setOnClickListener {
            // TODO
//                Toast.makeText(this@MainActivity, "build video input", Toast.LENGTH_SHORT).show()
//            }

//            actionInsertYoutube.setOnClickListener {
            // TODO
//                Toast.makeText(this@MainActivity, "build YouTube", Toast.LENGTH_SHORT).show()
//            }

//            actionInsertLink.setOnClickListener {
//                addLinkDialog()
//            }

//            actionInsertCheckbox.setOnClickListener {
//                mEditor.insertTodo()
//            }

//    private fun addLinkDialog() {
//        val mEditor = binding.contentEt
//        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
//        val inflater = layoutInflater
//        val dialogView = inflater.inflate(R.layout.custom_layout, null)
//        alertDialogBuilder.setView(dialogView)
//
//        val linkEditText = dialogView.findViewById<EditText>(R.id.dialog_link)
//        val yesBtn: LinearLayout = dialogView.findViewById(R.id.Add)
//        val linkTitleEditText = dialogView.findViewById<EditText>(R.id.dialog_link_title)
//
//        yesBtn.setOnClickListener {
//            val link = linkEditText.text.toString()
//            val linkTitle = linkTitleEditText.text.toString()
//            mEditor.insertLink(link, linkTitle)
//        }
//
//        alertDialogBuilder.setCancelable(true)
//        alertDialogBuilder.show()
//    }
