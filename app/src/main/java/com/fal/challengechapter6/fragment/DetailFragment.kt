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
import com.bumptech.glide.Glide
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentDetailBinding
import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.viewmodel.HomeViewModel
import com.fal.challengechapter6.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        //Bundle from adapter
        val getData = arguments?.getSerializable("dataTask") as ResponseDataTaskItem
        val idTask = getData.idTask
        val userid = getData.userId
        var category = getData.category
        val content = getData.content
        val title = getData.title
        val image = getData.image
        binding.vTitle.text = title
        Glide.with(this).load(image).into(binding.vImage)
        binding.vContent.text = content

        if (category == "favorite"){
            binding.btnFavorit.setImageResource(R.drawable.heart)
        }else{
            binding.btnFavorit.setImageResource(R.drawable.unheart)
        }

        binding.btnUpdate.setOnClickListener {
            val data = Bundle()
            data.putString("idTask", idTask)
            data.putString("userId", userid)
            data.putString("category", category)
            data.putString("content", content)
            data.putString("title", title)
            data.putString("image", image)
            findNavController().navigate(R.id.action_detailFragment_to_updateFragment, data)
            Log.d("DATA UPDATE", "$data")
        }

        binding.btnFavorit.setOnClickListener {
            if (category == "favorite"){
                category = "unfav"
                binding.btnFavorit.setImageResource(R.drawable.unheart)
                viewModel.callUpdateData("unfav", content, idTask, image, title, userid)
                viewModel.updateLiveData().observe(viewLifecycleOwner){
                    if (it != null) {
                        Log.d("UPDATE Favorite", "Update Success")
                    } else {
                    Log.d("UPDATE RETROFIT", "Data Null")
                    }
                }
            }else{
                category = "favorite"
                binding.btnFavorit.setImageResource(R.drawable.heart)
                viewModel.callUpdateData("favorite", content, idTask, image, title, userid)
                viewModel.updateLiveData().observe(viewLifecycleOwner){
                    if (it != null) {
                        Log.d("UPDATE Favorite", "Update Success")
                    } else {
                        Log.d("UPDATE RETROFIT", "Data Null")
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.delete))
                .setMessage(resources.getString(R.string.are_you_sure))
                .setCancelable(false)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                    dialog.cancel()
                }
                .setPositiveButton(resources.getString(R.string.delete)) { dialog, which ->
                    // Respond to positive button press
                    viewModel.callDeleteData(userid, idTask)
                    viewModel.deleteLiveData().observe(viewLifecycleOwner) {
                        if (it != null) {
                            Log.d("deleteFilm", it.toString())
                            Toast.makeText(context,
                                getString(R.string.delete_success),
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.i("DELETE TASK", "$getData")
                    findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
                }
                .show()
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