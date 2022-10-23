package com.fal.challengechapter6.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.fal.challengechapter6.MainActivity
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentLoginBinding
import com.fal.challengechapter6.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var userVM : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

      _binding = FragmentLoginBinding.inflate(inflater, container, false)
      return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Data Store


        binding.btnLocalEng.setOnClickListener {
            setLang("en")
        }
        binding.btnLocalIn.setOnClickListener {
            setLang("id")
        }

        binding.btnRegist.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registFragment)
        }

        binding.login.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            auth(username,password)
        }

    }

    private fun auth(username: String, password: String) {
        userVM = ViewModelProvider(this)[UserViewModel::class.java]
        userVM.getUser(username, password)
        userVM.liveUserList().observe(viewLifecycleOwner) {
            if (it == null) {
                binding.inputLayoutUsername.error = getString(R.string.invalid_username)
                binding.inputLayoutPass.error = getString(R.string.invalid_password)
                Toast.makeText(context, R.string.login_failed, Toast.LENGTH_SHORT).show()
                Log.i("LOGIN STATUS", it.toString())
            } else {
                Log.d(TAG, "auth success : $it")
                for (i in 0 until it.size){
                    if (username == it[i].username && password == it[i].password){
                        Log.d(TAG, "auth: ${it[i].username}")
                        //Save user Sessions in proto
                        userVM.updateProto(it[i].username, it[i].id)
                        //Clear Error
                        binding.inputLayoutUsername.error = null
                        binding.inputLayoutPass.error = null
                        //Navigate to Home
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show()
                        Log.i("User Data", it.toString())
                        Log.d(TAG, "auth success: ${it[i].username}, ${it[i].id}")
                    }else{
                        Log.d(TAG, "auth: username or password not match")
                    }
                }
            }
        }
    }

    private fun setLang(localCode: String) {
        val locale = Locale(localCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        val res = resources
        res.updateConfiguration(config, res.displayMetrics)
        val intent = Intent(activity, MainActivity::class.java)
        requireActivity().startActivity(intent)

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
        onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}