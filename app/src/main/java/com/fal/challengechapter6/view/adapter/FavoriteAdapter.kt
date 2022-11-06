package com.fal.challengechapter6.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fal.challengechapter6.R
import com.fal.challengechapter6.databinding.ItemListBinding
import com.fal.challengechapter6.model.ResponseDataTaskItem

class FavoriteAdapter(private var itemTask : List<ResponseDataTaskItem>): RecyclerView.Adapter<FavoriteAdapter.ViewHolder>(){
    class ViewHolder (val binding : ItemListBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView).load(itemTask[position].image).into(holder.binding.vImage)
        holder.binding.vTitle.text = itemTask[position].title
        holder.binding.btnDetail.setOnClickListener {
            val data = Bundle()
            data.putSerializable("dataTask", itemTask[position])
            Navigation.findNavController(it).navigate(R.id.action_favoriteFragment_to_detailFragment, data)
        }
    }

    override fun getItemCount(): Int {
        return itemTask.size
    }

    fun setData(data : ArrayList<ResponseDataTaskItem>){
        this.itemTask = data
    }
}