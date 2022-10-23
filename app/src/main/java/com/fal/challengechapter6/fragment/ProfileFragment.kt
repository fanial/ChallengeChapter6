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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
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
    }

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
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.lgout)) { dialog, which ->
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