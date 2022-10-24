package com.fal.challengechapter6.fragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.ActivityBlurBinding
import com.fal.challengechapter6.databinding.FragmentProfileBinding
import com.fal.challengechapter6.viewmodel.BlurViewModel
import com.fal.challengechapter6.workers.KEY_IMAGE_URI
import com.fal.challengechapter6.workers.KEY_PROGRESS

class BlurActivity : AppCompatActivity() {
    private val viewModel by viewModels<BlurViewModel>()

    private var _binding : ActivityBlurBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Image URI should be stored in the ViewModel; put it there then display
        val imageUriExtra = intent.getStringExtra(KEY_IMAGE_URI)
        viewModel.setImageUri(imageUriExtra)

        viewModel.imageUri?.let { imageUri ->
            Glide.with(this).load(imageUri).into(binding.vImage)
        }

        binding.btnBlur.setOnClickListener {
            viewModel.applyBlur(3)
        }

        binding.btnSeeFile.setOnClickListener {
            // Create an Intent to view the Image pointed to by the Output URI saved in the ViewModel
            Intent(Intent.ACTION_VIEW, viewModel.outputUri).let { actionViewIntent ->
                // Check if there is any activity to handle this Intent
                actionViewIntent.resolveActivity(packageManager)?.run {
                    // When we have found an activity, start the activity with the Intent
                    startActivity(actionViewIntent)
                }
            }
        }

        viewModel.outputWorkInfos.observe(this, Observer { workInfos ->
            if (!workInfos.isNullOrEmpty()) {
                // When WorkInfo Objects are generated

                // Pick the first WorkInfo object. There will be only one WorkInfo object
                // since the corresponding WorkRequest that was tagged is part of a unique work chain
                val workInfo = workInfos[0]

                // Check the work status
                if (workInfo.state.isFinished) {
                    // When the work is finished (i.e., SUCCEEDED / FAILED / CANCELLED),
                    // show and hide the appropriate views for the same
                    showWorkFinished()

                    // Read the final output Image URI string from the WorkInfo's Output Data
                    workInfo.outputData.getString(KEY_IMAGE_URI)
                        .takeIf { !it.isNullOrEmpty() }?.let { outputUriStr ->
                            // When we have the final Image URI

                            // Save the final Image URI string in the ViewModel
                            viewModel.setOutputUri(outputUriStr)
                            // Show the "See File" button
                            binding.btnSeeFile.visibility = View.VISIBLE
                        }

                } else {
                    // In other cases, show and hide the appropriate views for the same
                    showWorkInProgress()
                }
            }
        })

        viewModel.progressWorkInfos.observe(this, Observer { workInfos ->
            if (!workInfos.isNullOrEmpty()) {
                // When WorkInfo Objects are generated

                // Apply for all WorkInfo Objects
                workInfos.forEach { workInfo ->
                    if (workInfo.state == WorkInfo.State.RUNNING) {
                        // When the Work is in progress,
                        // obtain the Progress Data and update the Progress to ProgressBar
                        binding.progressCircular.progress = workInfo.progress.getInt(KEY_PROGRESS, 0)
                    }
                }
            }
        })

    }
    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        with(binding) {
            progressCircular.visibility = View.VISIBLE
            btnBlur.visibility = View.GONE
            btnSeeFile.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressCircular.visibility = View.GONE
            btnBlur.visibility = View.VISIBLE
            // Ensuring that the progress restarts at initial 0 after completion of Work
            progressCircular.progress = 0
        }
    }
}
