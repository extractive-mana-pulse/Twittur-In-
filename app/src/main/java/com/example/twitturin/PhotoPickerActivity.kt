package com.example.twitturin

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityPhotoPickerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoPickerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPhotoPickerBinding
    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickMultipleMediaLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize single media picker launcher
        pickSingleMediaLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this, "Failed picking media.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.data?.data?.path, Toast.LENGTH_SHORT).show()
                }
            }

        // Initialize multiple media picker launcher
        pickMultipleMediaLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this, "Failed picking media.", Toast.LENGTH_SHORT).show()
                } else {
                    val uris = it.data?.clipData ?: return@registerForActivityResult
                    var uriPaths = ""
                    for (index in 0 until uris.itemCount) {
                        uriPaths += uris.getItemAt(index).uri.path
                        uriPaths += "\n"
                    }

                    Toast.makeText(this, uriPaths, Toast.LENGTH_SHORT).show()

//                    snackbarHelper.snackbar(
//                        findViewById(R.id.photo_picker_root_layout),
//                        findViewById(R.id.photo_picker_root_layout),
//                        message = "SUCCESS: $uriPaths"
//                    )
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            binding.choosePictureBtn.setOnClickListener {
                pickSingleMediaLauncher.launch(
                    Intent(MediaStore.ACTION_PICK_IMAGES)
                        .apply {
                            type = "image/*"
                        }
                )
            }
            binding.choosePictureBtn.setOnClickListener {
                pickMultipleMediaLauncher.launch(
                    Intent(MediaStore.ACTION_PICK_IMAGES)
                        .apply {
                            type = "image/*"
                            putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 3)
                        }
                )
            }
        }
    }
}