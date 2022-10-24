package com.fal.challengechapter6.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class SaveImageToFileWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters)  {
    override fun doWork(): Result {
        // Show a Notification when the work starts
        makeStatusNotification("Saving Image...", applicationContext)
        // Slow down the start so that it is easier to see each WorkRequest start
        sleep()

        return try {
            // Try saving the image to MediaStore filesystem
            saveImageToMedia(
                applicationContext,
                inputData.getString(KEY_IMAGE_URI))?.let { imageUriStr: String ->
                // On Success

                // Return as successful with the output Data containing the Uri
                // to the permanently saved blurred image file, in order to make it available
                // to other workers for further operations
                Result.success(workDataOf(KEY_IMAGE_URI to imageUriStr))
            } ?: run {
                // On Failure to save the image

                // Log the error
                Log.e(TAG,"Error: Image could not be saved to MediaStore")
                // Return as failed
                Result.failure()
            }
        } catch (throwable: Throwable) {
            // Log the error
            Log.e(TAG, "Error occurred while saving image to MediaStore", throwable)
            // Return as failed
            Result.failure()
        }
    }
}
