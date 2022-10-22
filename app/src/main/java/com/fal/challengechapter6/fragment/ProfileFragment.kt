package com.fal.challengechapter6.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentProfileBinding
import com.fal.challengechapter6.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var model : UserViewModel
    private lateinit var share : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var id : String
    private lateinit var oldPassword : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(this)[UserViewModel::class.java]
        share = requireActivity().getSharedPreferences("account", Context.MODE_PRIVATE)
        share.getString("username","username")
        editor = share.edit()
        id = share.getString("id", "").toString()

        getDataUser()

        Log.d("Homescreen", "Username : $share")
        binding.btnUpdate.setOnClickListener {
            updateUser()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun updateUser() {
        val email = binding.vEmail.text.toString()
        val username = binding.vUsername.text.toString()
        val password = binding.vPassword.text.toString()
            model.putUser(email, id, username, password)
            model.liveUpdateUser().observe(viewLifecycleOwner){
                if (it != null) {
                    if (!oldPassword.equals(password)){
                        binding.inputLayoutPass.error = getString(R.string.invalid_password)
                    }
                    Toast.makeText(requireContext(), getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.lgout))
            .setMessage(resources.getString(R.string.are_you_sure))
            .setCancelable(false)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.lgout)) { dialog, which ->
                // Respond to positive button press
                editor.clear()
                editor.apply()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
            .show()
    }

    private fun getDataUser() {
        model.getUserbyId(id)
        model.liveUserid().observe(viewLifecycleOwner){
            if (it != null){
                binding.vUsername.setText(it.username)
                binding.vEmail.setText(it.email)
                binding.vPassword.setText(it.password)
                oldPassword = it.password
            }
        }
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