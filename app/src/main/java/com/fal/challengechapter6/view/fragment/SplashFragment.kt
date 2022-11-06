package com.fal.challengechapter6.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentFirstBinding
import com.fal.challengechapter6.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var model: UserViewModel
    var userId = ""
    var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val splashTime : Long = 3000

        Handler(Looper.myLooper()!!).postDelayed({
            detectAcc()
        }, splashTime)

    }

    private fun detectAcc() {
        model = ViewModelProvider(this).get(UserViewModel::class.java)
        model.dataUser.observe(viewLifecycleOwner){
                if(it.userId.equals("")){
                    Log.d("SESSIONS", "UserID Null : $userId, $username")
                    Navigation.findNavController(binding.root).navigate(R.id.action_splashFragment_to_loginFragment)
                } else {
                    userId = it.userId
                    username = it.nama
                    Log.d("SESSIONS", "UserID : $userId, $username")
                    Navigation.findNavController(binding.root).navigate(R.id.action_splashFragment_to_homeFragment)

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