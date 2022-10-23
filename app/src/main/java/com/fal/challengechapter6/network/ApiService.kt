package com.fal.challengechapter6.network

import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.model.ResponseDataUserItem
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    //Task
    @GET("user/{id}/task")
    fun getTask(
        @Path("id") userId: String

    ) : Call<List<ResponseDataTaskItem>>

    @GET("user/{id}/task")
    fun getFav(
        @Path("id") userId: String,
        @Query("category") favorite : String = "favorite"
    ) : Call<List<ResponseDataTaskItem>>

    @GET("user/{id}/task/{idTask}")
    fun getTaskId(
        @Path("id") userId: String,
        @Path("idTask") idTask: String
    ) : Call<ResponseDataTaskItem>

    @PUT("user/{id}/task/{idTask}")
    fun putData(
        @Path("id") userId: String,
        @Path("idTask") idTask : String,
        @Body request : ResponseDataTaskItem
    ) : Call<ResponseDataTaskItem>

    @DELETE("user/{id}/task/{idTask}")
    fun delData(
        @Path("id") userId : String,
        @Path("idTask") idTask : String
    ) : Call<String>

    //User
    @GET("user")
    fun getUser() : Call<List<ResponseDataUserItem>>

    @GET("user/{id}")
    fun getUserId(
        @Path("id") userId: String
    ) : Call<ResponseDataUserItem>

    @PUT("user/{id}")
    fun putUser(
        @Path("id") userId: String,
        @Body request: ResponseDataUserItem
    ) : Call<ResponseDataUserItem>

    @POST("user")
    fun postUser(@Body user : ResponseDataUserItem) : Call<ResponseDataUserItem>
}