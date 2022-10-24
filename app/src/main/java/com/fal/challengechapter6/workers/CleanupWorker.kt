package com.fal.challengechapter6.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Thread.sleep

class CleanupWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        // Show a Notification when the work starts
        makeStatusNotification("Cleaning up old temporary files", applicationContext)
        // Slow down the start so that it is easier to see each WorkRequest start
        sleep()

        return try {
            // Clean up any temporary image files if present
            cleanUpTempFiles(applicationContext)
            // Return as successful
            Result.success()
        } catch (throwable: Throwable) {
            // Log the error
            Log.e(TAG, "Error occurred during the cleanup of temporary image files", throwable)
            // Return as failed
            Result.failure()
        }
    }
}