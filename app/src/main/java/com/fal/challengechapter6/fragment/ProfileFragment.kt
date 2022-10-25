package com.fal.challengechapter6.fragment

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentProfileBinding
import com.fal.challengechapter6.viewmodel.BlurViewModel
import com.fal.challengechapter6.viewmodel.BlurViewModelFactory
import com.fal.challengechapter6.viewmodel.UserViewModel
import com.fal.challengechapter6.workers.KEY_IMAGE_URI
import com.fal.challengechapter6.workers.KEY_PROGRESS
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    companion object {
        // Permission request constant for External storage access
        const val REQUEST_CODE_PERMISSIONS = 101

        // Bundle Constant to save the count of permission requests retried
        const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"

        // Constant to limit the number of permission request retries
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
    }

    // List of permissions required by the app to access external storage
    // to allow the user to select an image
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val viewModel: BlurViewModel by viewModels { BlurViewModelFactory(requireActivity().application) }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            viewModel.setImageUri(result.toString())
            if (result != null){
                viewModel.applyBlur(1)
            }else{
                Log.d(TAG, "Image URI Status: Unexpected URI")
            }
        }

    private val profilePic =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            binding.btnImage.setImageURI(result)
        }

    // Stores the count of permission requests retried
    private var permissionRequestCount: Int = 0
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: UserViewModel
    var id = ""
    var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make sure the app has correct permissions to run
        requestPermissionsIfNecessary()

        // When activity is reloaded after configuration change
        savedInstanceState?.let {
            // Restore the permission request count
            permissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
        }

        model = ViewModelProvider(this)[UserViewModel::class.java]
        model.dataUser.observe(viewLifecycleOwner){
            if(it == null){
                Log.d("SESSIONS", "UserID Null : $id, $username")
            }else{
                id = it.userId
                getDataUser(id)
            }
        }


        binding.btnUpdate.setOnClickListener {
            updateUser()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnImage.setOnClickListener {
            requireActivity().intent.type = "image/*"
            profilePic.launch("image/*")
        }

        binding.btnBlur.setOnClickListener {
            requireActivity().intent.type = "image/*"
            galleryResult.launch("image/*")
        }

        viewModel.outputWorkInfos.observe(viewLifecycleOwner) { workInfos ->
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
                        }

                } else {
                    // In other cases, show and hide the appropriate views for the same
                    showWorkInProgress()
                }
            }
        }

        viewModel.progressWorkInfos.observe(viewLifecycleOwner) { workInfos ->
            if (!workInfos.isNullOrEmpty()) {
                // When WorkInfo Objects are generated

                // Apply for all WorkInfo Objects
                workInfos.forEach { workInfo ->
                    if (workInfo.state == WorkInfo.State.RUNNING) {
                        // When the Work is in progress,
                        // obtain the Progress Data and update the Progress to ProgressBar
                        binding.progressCircular.progress =
                            workInfo.progress.getInt(KEY_PROGRESS, 0)
                    }
                }
            }
        }
    }

    private fun showWorkInProgress() {
        with(binding) {
            progressCircular.visibility = View.VISIBLE
            btnBlur.visibility = View.GONE
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

    private fun requestPermissionsIfNecessary() {
        // Check if all required permissions are granted
        if (!checkAllPermissions()) {
            // When all required permissions are not granted yet

            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                // When the number of permission request retried is less than the max limit set
                permissionRequestCount += 1 // Increment the number of permission requests done
                // Request the required permissions for external storage access
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                // Disable the "Select Image" button when access is denied by the user
                binding.btnImage.isEnabled = false
                binding.btnBlur.isEnabled = false
                // When the number of permission request retried exceeds the max limit set
                // Show a toast about how to update the permission for storage access
                Toast.makeText(
                    context,
                    R.string.set_permissions_in_settings,
                    Toast.LENGTH_LONG
                ).show()
            }
        }else{
            Toast.makeText(context, "Access Denied", Toast.LENGTH_LONG).show()
            // Disable the "Select Image" button when access is denied by the user
            binding.btnImage.isEnabled = false
            binding.btnBlur.isEnabled = false
        }
    }

    private fun checkAllPermissions(): Boolean {
        // Boolean state to indicate all permissions are granted
        var hasPermissions = true
        // Verify all permissions are granted
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        // Return the state of all permissions granted
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(requireContext(), permission) ==
            PackageManager.PERMISSION_GRANTED

    private fun updateUser() {
        val email = binding.vEmail.text.toString()
        val username = binding.vUsername.text.toString()
        val password = binding.vPassword.text.toString()
        val repass = binding.vRePassword.text.toString()
            model.liveUpdateUser().observe(viewLifecycleOwner){
                if (it != null) {
                    if (repass != password){
                        binding.inputLayoutPass.error = getString(R.string.pass_not_match)
                        binding.inputLayoutRePass.error = getString(R.string.pass_not_match)
                    }else{
                        model.putUser(email, id, username, password)
                        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                        Toast.makeText(requireContext(), getString(R.string.logout_for_update), Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.lgout))
            .setMessage(resources.getString(R.string.are_you_sure))
            .setCancelable(false)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.lgout)) { _, _ ->
                // Respond to positive button press
                model.delProto()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
            .show()
    }

    private fun getDataUser(id : String) {
        model.getUserbyId(id)
        model.liveUserid().observe(viewLifecycleOwner){
            if (it != null){
                binding.vUsername.setText(it.username)
                binding.vEmail.setText(it.email)
                binding.vPassword.setText(it.password)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            // For External Storage access permission request
            REQUEST_CODE_PERMISSIONS -> requestPermissionsIfNecessary() // no-op if permissions are granted already.
            // For other requests, delegate to super
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the permission request count on rotation
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this.remove()
                activity?.onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}