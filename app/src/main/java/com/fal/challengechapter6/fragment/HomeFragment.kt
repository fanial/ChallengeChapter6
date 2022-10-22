package com.fal.challengechapter6.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fal.challengechapter6.ListAdapter
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentHomeBinding
import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.network.ApiClient
import com.fal.challengechapter6.viewmodel.HomeViewModel
import com.fal.challengechapter6.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter : ListAdapter
    private lateinit var model: UserViewModel
    var userId = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Data Store
        model = ViewModelProvider(this)[UserViewModel::class.java]
        model.dataUser.observe(viewLifecycleOwner){
            userId = it.userId
            Log.d(TAG, "onActivityCreated: $userId")
        }

        //VM
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        if (userId.equals("")){
            Log.d(TAG, "onActivityCreated ELSE: ${userId}")
        }else{
            Log.d(TAG, "onActivityCreated IF: ${userId}")
            viewModel.callAllData(userId!!)
            viewModel.allLiveData().observe(viewLifecycleOwner) {
                Log.d(TAG, "onActivityCreated OBSERVER: ${it}")
                //adapter = ListAdapter(it!!)
                if (it != null){
                    adapter.setData(it as ArrayList<ResponseDataTaskItem>)
                    binding.rvTask.adapter = ListAdapter(it)
                    adapter = ListAdapter(it)
                    binding.rvTask.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
        }

        adapter = ListAdapter(ArrayList())
        binding.rvTask.adapter = adapter
        binding.rvTask.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //Profile
        binding.btnProfil.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.callAllData(userId!!)
    }
}