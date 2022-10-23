package com.fal.challengechapter6.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentRegistBinding
import com.fal.challengechapter6.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistFragment : Fragment() {

    private var _binding : FragmentRegistBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_registFragment_to_loginFragment)
        }

        binding.btnRegist.setOnClickListener {
            registUser()
        }
    }

    private fun registUser() {
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repass = binding.repassword.text.toString()

        if (username.isEmpty() && email.isEmpty() && password.isEmpty()){
            binding.inputLayoutPass.error = getString(R.string.empty_field)
            binding.inputLayoutRePass.error = getString(R.string.empty_field)
            binding.inputLayoutUsername.error = getString(R.string.empty_field)
            binding.inputLayoutEmail.error = getString(R.string.empty_field)
            Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
        }else if(password != repass){
            binding.inputLayoutPass.error = getString(R.string.pass_not_match)
            binding.inputLayoutRePass.error = getString(R.string.pass_not_match)
        }else{
            val model = ViewModelProvider(this)[UserViewModel::class.java]
            model.postDataUser(email,"",password,username)
            model.liveUser().observe(requireActivity()) {
                if (it != null) {
                    findNavController().navigate(R.id.action_registFragment_to_loginFragment)
                    Toast.makeText(context, getString(R.string.regist_success), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, getString(R.string.regist_failed), Toast.LENGTH_SHORT)
                        .show()
                    Log.i("REGISTER STATUS", "Register Failed")
                }
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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}