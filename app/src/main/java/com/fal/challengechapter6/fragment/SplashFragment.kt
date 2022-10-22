package com.fal.challengechapter6.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentFirstBinding

@Suppress("DEPRECATION")
class SplashFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var sharedPref : SharedPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)!!
        val splashTime : Long = 3000

        Handler().postDelayed({
            detectAkun()
        }, splashTime)

    }

    private fun detectAkun() {
        sharedPref = requireActivity().getSharedPreferences("account", Context.MODE_PRIVATE)

        Handler(Looper.myLooper()!!).postDelayed({
            if(sharedPref.getString("id","").equals("")){
                Navigation.findNavController(binding.root).navigate(R.id.action_splashFragment_to_loginFragment)
            } else {
                Navigation.findNavController(binding.root).navigate(R.id.action_splashFragment_to_homeFragment)
            }
        },3000)
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