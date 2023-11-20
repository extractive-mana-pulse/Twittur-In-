package com.example.twitturin.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.presentation.mvvm.MainViewModel
import com.example.twitturin.presentation.mvvm.Repository
import com.example.twitturin.presentation.mvvm.ViewModelFactory
import com.example.twitturin.presentation.sealeds.PostTweet


class PublicPostFragment : Fragment() {

    private lateinit var binding : FragmentPublicPostBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPublicPostBinding.inflate(layoutInflater)
        return binding.root
    }

//    @Composable
//    fun ControlWrapper(
//        selected: Boolean,
//        selectedColor: Color = MaterialTheme.colors.primary,
//        unselectedColor: Color = MaterialTheme.colors.secondaryVariant,
//        onChangeClick: (Boolean) -> Unit,
//        onClick: () -> Unit,
//        content: @Composable () -> Unit
//    ) {
//        Box(
//            modifier = Modifier
//                .clip(RoundedCornerShape(size = 6.dp))
//                .clickable {
//                    onClick()
//                    onChangeClick(!selected)
//                }
//                .background(
//                    if (selected) selectedColor
//                    else unselectedColor
//                )
//                .border(
//                    width = 1.dp,
//                    color = Color.LightGray,
//                    shape = RoundedCornerShape(size = 6.dp)
//                )
//                .padding(all = 8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            content()
//        }
//    }
//
//
//    @OptIn(ExperimentalLayoutApi::class)
//    @Composable
//    fun EditorControls(
//        modifier: Modifier = Modifier,
//        state: RichTextState,
//        onBoldClick: () -> Unit,
//        onItalicClick: () -> Unit,
//        onUnderlineClick: () -> Unit,
//        onTitleClick: () -> Unit,
//        onSubtitleClick: () -> Unit,
//        onTextColorClick: () -> Unit,
//        onStartAlignClick: () -> Unit,
//        onEndAlignClick: () -> Unit,
//        onCenterAlignClick: () -> Unit,
//        onExportClick: () -> Unit,
//    ) {
//        var boldSelected by rememberSaveable { mutableStateOf(false) }
//        var italicSelected by rememberSaveable { mutableStateOf(false) }
//        var underlineSelected by rememberSaveable { mutableStateOf(false) }
//        var titleSelected by rememberSaveable { mutableStateOf(false) }
//        var subtitleSelected by rememberSaveable { mutableStateOf(false) }
//        var textColorSelected by rememberSaveable { mutableStateOf(false) }
//        var linkSelected by rememberSaveable { mutableStateOf(false) }
//        var alignmentSelected by rememberSaveable { mutableIntStateOf(0) }
//
//        var showLinkDialog by remember { mutableStateOf(false) }

//            AnimatedVisibility(visible = showLinkDialog) {
//                LinkDialog(
//                    onDismissRequest = {
//                        showLinkDialog = false
//                        linkSelected = false
//                    },
//                    onConfirmation = { linkText, link ->
//                        state.addLink(
//                            text = linkText,
//                            url = link
//                        )
//                        showLinkDialog = false
//                        linkSelected = false
//                    }
//                )
//            }

//        FlowRow(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 10.dp)
//                .padding(bottom = 24.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            ControlWrapper(
//                selected = boldSelected,
//                onChangeClick = { boldSelected = it },
//                onClick = onBoldClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatBold,
//                    contentDescription = "Bold Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = italicSelected,
//                onChangeClick = { italicSelected = it },
//                onClick = onItalicClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatItalic,
//                    contentDescription = "Italic Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = underlineSelected,
//                onChangeClick = { underlineSelected = it },
//                onClick = onUnderlineClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatUnderlined,
//                    contentDescription = "Underline Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = titleSelected,
//                onChangeClick = { titleSelected = it },
//                onClick = onTitleClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Title,
//                    contentDescription = "Title Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = subtitleSelected,
//                onChangeClick = { subtitleSelected = it },
//                onClick = onSubtitleClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatSize,
//                    contentDescription = "Subtitle Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = textColorSelected,
//                onChangeClick = { textColorSelected = it },
//                onClick = onTextColorClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatColorText,
//                    contentDescription = "Text Color Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = linkSelected,
//                onChangeClick = { linkSelected = it },
//                onClick = { showLinkDialog = true }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.AddLink,
//                    contentDescription = "Link Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = alignmentSelected == 0,
//                onChangeClick = { alignmentSelected = 0 },
//                onClick = onStartAlignClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatAlignLeft,
//                    contentDescription = "Start Align Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = alignmentSelected == 1,
//                onChangeClick = { alignmentSelected = 1 },
//                onClick = onCenterAlignClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatAlignCenter,
//                    contentDescription = "Center Align Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = alignmentSelected == 2,
//                onChangeClick = { alignmentSelected = 2 },
//                onClick = onEndAlignClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.FormatAlignRight,
//                    contentDescription = "End Align Control",
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//            ControlWrapper(
//                selected = true,
//                selectedColor = MaterialTheme.colors.onError,
//                onChangeClick = { },
//                onClick = onExportClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Save,
//                    contentDescription = "Export Control",
//                    tint = MaterialTheme.colors.secondary
//                )
//            }
//        }
//    }
//
//    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//    @Preview
//    @Composable
//    fun MainScreen() {
//        val state = rememberRichTextState()
//        val titleSize = MaterialTheme.typography.h6.fontSize
//        val subtitleSize = MaterialTheme.typography.h4.fontSize
//
//        Scaffold {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(all = 20.dp)
//                    .padding(bottom = it.calculateBottomPadding())
//                    .padding(top = it.calculateTopPadding()),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                EditorControls(
//                    modifier = Modifier.weight(2f),
//                    state = state,
//                    onBoldClick = {
//                        state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
//                    },
//                    onItalicClick = {
//                        state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
//                    },
//                    onUnderlineClick = {
//                        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
//                    },
//                    onTitleClick = {
//                        state.toggleSpanStyle(SpanStyle(fontSize = titleSize))
//                    },
//                    onSubtitleClick = {
//                        state.toggleSpanStyle(SpanStyle(fontSize = subtitleSize))
//                    },
//                    onTextColorClick = {
//                        state.toggleSpanStyle(SpanStyle(color = Color.Red))
//                    },
//                    onStartAlignClick = {
//                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Start))
//                    },
//                    onEndAlignClick = {
//                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.End))
//                    },
//                    onCenterAlignClick = {
//                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
//                    },
//                    onExportClick = {
//                        Log.d("Editor", state.toHtml())
//                    }
//                )
//                RichTextEditor(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(8f),
//                    state = state,
//                )
//            }
//
//        }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment  = this

//        requireActivity().findViewById<ComposeView>(R.id.compose).setContent {
//            MaterialTheme {
//                Surface {
//                    MainScreen()
//                }
//            }
//        }

        val sessionManager = SessionManager(requireContext())

        val token = sessionManager.getToken()
        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
        Toast.makeText(requireContext(), token, Toast.LENGTH_SHORT).show()

        binding.btnTweet.setOnClickListener {
            if (!token.isNullOrEmpty()){
                val tweetContent = binding.contentEt.text.toString()
                viewModel.postTheTweet(tweetContent, token)
            } else {
                Toast.makeText(requireContext(), "something went wrong my G", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.postTweetResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PostTweet.Success -> {
                    findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment)
                    Toast.makeText(requireContext(), result.response.toString(), Toast.LENGTH_SHORT).show()
                }

                is PostTweet.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun cancelBtn(){
        requireActivity().onBackPressed()
    }

//    fun tweetBtn(){
        // get logic from api to post a tweet
//    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicPostFragment()
    }
}