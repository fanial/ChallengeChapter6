package com.fal.challengechapter6.view.fragment

import android.content.ContentValues
import android.content.Context
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
import com.fal.challengechapter6.databinding.FragmentUpdateBinding
import com.fal.challengechapter6.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get data
        val getId = arguments?.getString("idTask")
        val getUser = arguments?.getString("userId")
        val getTitle = arguments?.getString("title")
        val getCategory = arguments?.getString("category")
        val getContent = arguments?.getString("content")
        val getImage = arguments?.getString("image")
        //set Data
        Log.d(ContentValues.TAG, "onViewCreated: $getId,$getUser")
        binding.vContent.setText(getContent)
        binding.vImage.setText(getImage)
        binding.vTitle.setText(getTitle)

        binding.btnUpdate.setOnClickListener {
            requireActivity().run {
                updateData(getCategory!!,binding.vContent.text.toString(), getId!!,binding.vImage.text.toString(), binding.vTitle.text.toString(), getUser!!)
                findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
                Log.d("UPDATE STATUS", "Update Success")
            }
        }
    }

    private fun updateData(
        category: String,
        content: String,
        idTask: String,
        image: String,
        title: String,
        id: String,
    ) {
        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.callUpdateData(category, content, idTask, image, title, id)
        viewModel.updateLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_SHORT).show()
            } else {
                Log.d("UPDATE RETROFIT", "Data Null")
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