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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var share : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Shared Preference
        share = requireActivity().getSharedPreferences("account", Context.MODE_PRIVATE)
        val name = share.getString("username","username")
        Log.d("Homescreen", "Username : $name")

        //Bundle from adapter
        val getData = arguments?.getSerializable("dataTask") as ResponseDataTaskItem
        val idTask = getData.idTask
        val userid = getData.userId
        val category = getData.category
        val content = getData.content
        val title = getData.title
        val image = getData.image
        binding.vTitle.text = title
        Glide.with(this).load(image).into(binding.vImage)
        binding.vContent.text = content
        binding.vCategory.text = category

        binding.btnUpdate.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(resources.getString(R.string.under_construc))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.previous)) { dialog, which ->
                    dialog.cancel()
                }
                .show()

            /**
            val data = Bundle()

            data.putString("idTask", idTask)
            data.putString("userId", userid)
            data.putString("category", category)
            data.putString("content", content)
            data.putString("title", title)
            data.putString("image", image)

            data.putSerializable("update", getData)
            findNavController().navigate(R.id.action_detailFragment_to_updateFragment, data)
            Log.d("DATA UPDATE", "${data}")
             **/
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
                    val model = ViewModelProvider(this)[HomeViewModel::class.java]
                    model.callDeleteData(userid, idTask)
                    model.deleteLiveData().observe(viewLifecycleOwner) {
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