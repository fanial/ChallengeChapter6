package com.fal.challengechapter6.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseDataTaskItem(
    @SerializedName("category")
    val category: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("idTask")
    val idTask: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: String
) : Serializable