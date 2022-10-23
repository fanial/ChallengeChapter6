package com.fal.challengechapter6.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fal.challengechapter6.model.ResponseDataUserItem
import com.fal.challengechapter6.network.ApiService
import com.fal.challengechapter6.repository.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val api : ApiService, application: Application) : AndroidViewModel(application) {
    //Data Store
    private val repository = UserPrefRepository(application)
    val dataUser = repository.readProto.asLiveData()

    fun updateProto(nama : String, userId : String) = viewModelScope.launch {
        repository.saveDataProto(nama, userId)
    }

    fun delProto() = viewModelScope.launch {
        repository.deleteData()
    }

    //Live Data
    var userData : MutableLiveData<ResponseDataUserItem?> = MutableLiveData()
    var userList : MutableLiveData<List<ResponseDataUserItem>?> = MutableLiveData()

    fun liveUser() : MutableLiveData<ResponseDataUserItem?>{
        return userData
    }

    fun liveUserid() : MutableLiveData<ResponseDataUserItem?>{
        return userData
    }

    fun liveUpdateUser() : MutableLiveData<ResponseDataUserItem?>{
        return userData
    }

    fun liveUserList() : MutableLiveData<List<ResponseDataUserItem>?>{
        return userList
    }


    //Retrofit
    fun getUser(username: String, password: String){
        api.getUser()
            .enqueue(object : Callback<List<ResponseDataUserItem>>{
                override fun onResponse(
                    call: Call<List<ResponseDataUserItem>>,
                    response: Response<List<ResponseDataUserItem>>,
                ) {
                    if (response.isSuccessful){
                        userList.postValue(response.body()!!)
                    }else{
                        userList.postValue(null)
                    }
                }

                override fun onFailure(call: Call<List<ResponseDataUserItem>>, t: Throwable) {
                    userList.postValue(null)
                }

            })
    }
    fun postDataUser(email : String, id : String, password : String, username : String){
        api.postUser(ResponseDataUserItem(email, id, password, username))
            .enqueue(object : Callback<ResponseDataUserItem>{
                override fun onResponse(
                    call: Call<ResponseDataUserItem>,
                    response: Response<ResponseDataUserItem>,
                ) {
                    if (response.isSuccessful){
                        userData.postValue(response.body())
                    }else{
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseDataUserItem>, t: Throwable) {
                    userData.postValue(null)
                }
            })
    }

    fun putUser(email: String, id: String, username: String, password: String){
        api.putUser(id, ResponseDataUserItem(email, id, password, username))
            .enqueue(object : Callback<ResponseDataUserItem>{
                override fun onResponse(
                    call: Call<ResponseDataUserItem>,
                    response: Response<ResponseDataUserItem>,
                ) {
                    if (response.isSuccessful){
                        userData.postValue(response.body())
                    }else{
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseDataUserItem>, t: Throwable) {
                    userData.postValue(null)
                }

            })
    }

    fun getUserbyId(id: String){
        api.getUserId(id)
            .enqueue(object : Callback<ResponseDataUserItem>{
                override fun onResponse(
                    call: Call<ResponseDataUserItem>,
                    response: Response<ResponseDataUserItem>,
                ) {
                    if (response.isSuccessful){
                        userData.postValue(response.body())
                    }else{
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseDataUserItem>, t: Throwable) {
                    userData.postValue(null)
                }

            })
    }
}