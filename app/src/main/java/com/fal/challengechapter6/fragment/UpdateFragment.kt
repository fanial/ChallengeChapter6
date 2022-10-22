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
import com.fal.challengechapter6.databinding.FragmentUpdateBinding
import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.viewmodel.HomeViewModel

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var share : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Shared Preference
        share = requireActivity().getSharedPreferences("account", Context.MODE_PRIVATE)
        val name = share.getString("username","username")
        Log.d("Homescreen", "Username : $name")

        binding.btnUpdate.setOnClickListener {
            requireActivity().run {
                //get data
                /**
                val getId = arguments?.getString("idTask", "0")
                val getUser = arguments?.getString("userId", "0")
                val getTitle = arguments?.getString("title","title")
                val getCategory = arguments?.getString("category","category")
                val getContent = arguments?.getString("content", "content")
                val getImage = arguments?.getString("image","image")
                **/
                val getUpdate = arguments?.getSerializable("update") as ResponseDataTaskItem
                //set Data
                binding.vCategory.setText(getUpdate.category)
                binding.vContent.setText(getUpdate.content)
                binding.vImage.setText(getUpdate.image)
                binding.vTitle.setText(getUpdate.title)

                val getUser = getUpdate.userId
                val getId = getUpdate.idTask
                val getCategory = getUpdate.category
                val getContent = getUpdate.content
                val getImage = getUpdate.image
                val getTitle = getUpdate.title

                updateData(getUser, getId, getCategory, getContent, getImage, getTitle)
                findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
                Log.d("UPDATE STATUS", "Update Success")
            }

        }
    }

    private fun updateData(
        id: String,
        idTask: String,
        title: String,
        category: String,
        content: String,
        image: String,
    ) {
        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.callUpdateData(title, category, idTask, content, image, id )
        viewModel.updateLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, "Update Data Success", Toast.LENGTH_SHORT).show()
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