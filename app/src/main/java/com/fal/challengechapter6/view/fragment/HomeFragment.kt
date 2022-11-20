package com.fal.challengechapter6.view.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.FragmentHomeBinding
import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.view.adapter.ListAdapter
import com.fal.challengechapter6.viewmodel.HomeViewModel
import com.fal.challengechapter6.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : ListAdapter
    private lateinit var model: UserViewModel
    var userId = ""
    var username = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Show Data
        showData()
        //Set RV
        adapter = ListAdapter(ArrayList())
        binding.rvTask.adapter = adapter
        binding.rvTask.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //Profile
        binding.btnProfil.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        binding.btnFavorit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_favoriteFragment)
        }

        //cek firebase
        /**
        val crashButton = Button(context)
        crashButton.text = "Test Crash"
        binding.btnAdd.setOnClickListener {
        throw RuntimeException("Test Crash") // Force a crash
        }
        **/

    }

    private fun showData() {
        model = ViewModelProvider(this)[UserViewModel::class.java]
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        model.dataUser.observe(viewLifecycleOwner) { it ->
            userId = it.userId
            username = it.nama
                Log.d(TAG, "UserID : $userId, $username")

            if (it.equals("")) {
                Log.d(TAG, "UserID Null : $userId")
            } else {
                binding.welcomeBar.text = buildString {
                    append(getString(R.string.wel))
                    append(username)
                }
                viewModel.callAllData(userId)
                viewModel.allLiveData().observe(viewLifecycleOwner) {
                    if (it != null) {
                        adapter.setData(it as ArrayList<ResponseDataTaskItem> /* = java.util.ArrayList<com.fal.challengechapter6.model.ResponseDataTaskItem> */)
                        binding.rvTask.adapter = ListAdapter(it)
                        adapter = ListAdapter(it)
                        binding.rvTask.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showData()
    }
}