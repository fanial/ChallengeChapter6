package com.fal.challengechapter6.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.fal.challengechapter6.workers.*

class BlurViewModel(application: Application) : AndroidViewModel(application) {
    //  var untuk instance  WorkManager di ViewModel
    private val workManager = WorkManager.getInstance(application).also {
        it.pruneWork()
    }
    private var imageUri: Uri? = null
    private var outputUri: Uri? = null

    // LiveData for SaveToImageFileWorker's WorkInfo objects to retrieve its status and output Data
    val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)

    // LiveData for BlurWorker's WorkInfo objects to retrieve its status and Progress Data
    val progressWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_PROGRESS)

    //    make WorkRequest & beritahu WM untuk jalankan
    internal fun applyBlur(blurLevel: Int) {
        // Create a one-off work request for cleaning any temporary image files
        val cleanUpRequest = OneTimeWorkRequest.from(CleanupWorker::class.java)

        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(createInputDataForUri(blurLevel))
            .build()

        // Configure charging constraint and available storage constraint for SaveImageToFileWorker
        val saveImageToFileConstraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresStorageNotLow(true)
            .build()

        // Create a one-off work request for saving the blurred image to MediaStore filesystem
        val saveImageToFileRequest = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag(TAG_OUTPUT) // Use Tag to get its status and output Data
            .setConstraints(saveImageToFileConstraints) // Add Constraints to be satisfied for Work
            .build()

        // Execute clean up first
        workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME, // A unique name identifying this chain of work
            ExistingWorkPolicy.REPLACE, // stop and replace the current work chain if any
            cleanUpRequest
        )
            .then(blurRequest) // then, blur the selected image
            .then(saveImageToFileRequest) // then, save the blurred image
            .enqueue() // enqueue and schedule the chain of work in the background thread

    }

    //create URI img
    private fun createInputDataForUri(blurLevel: Int): Data = workDataOf(
        // Uri to the Image to be blurred
        KEY_IMAGE_URI to imageUri.toString(),
        // Level of blur to be applied on the Image
        KEY_BLUR_LEVEL to blurLevel
    )

    private fun uriOrNull(uriString: String?): Uri? =
        uriString.takeIf { !it.isNullOrEmpty() }?.let { Uri.parse(it) }

    //setter
    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }
    //getter
    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }


}


@Suppress("UNCHECKED_CAST")
class BlurViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BlurViewModel::class.java)) {
            BlurViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}