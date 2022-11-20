package com.fal.challengechapter6.view.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fal.challengechapter6.view.adapter.FavoriteAdapter
import com.fal.challengechapter6.databinding.FragmentFavoriteBinding
import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.viewmodel.HomeViewModel
import com.fal.challengechapter6.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var model: UserViewModel
    private lateinit var adapter : FavoriteAdapter
    var userId = ""
    var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(this)[UserViewModel::class.java]
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        //Set RV
        adapter = FavoriteAdapter(ArrayList())
        binding.rvFavorite.adapter = adapter
        binding.rvFavorite.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        model.dataUser.observe(viewLifecycleOwner) { it ->
            userId = it.userId
            username = it.nama
            Log.d(ContentValues.TAG, "UserID : $userId, $username")

            if (it.equals("")) {
                Log.d(ContentValues.TAG, "UserID Null : $userId")
            } else {
                viewModel.callAllFavorite(userId)
                viewModel.allLiveData().observe(viewLifecycleOwner) {
                    if (it != null) {
                        adapter.setData(data = it as ArrayList<ResponseDataTaskItem> /* = java.util.ArrayList<com.fal.challengechapter6.model.ResponseDataTaskItem> */)
                        binding.rvFavorite.adapter = FavoriteAdapter(it)
                        adapter = FavoriteAdapter(it)
                        binding.rvFavorite.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    }
                }
            }
        }
    }
}