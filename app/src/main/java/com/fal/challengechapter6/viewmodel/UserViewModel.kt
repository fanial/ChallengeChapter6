package com.fal.challengechapter6.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fal.challengechapter6.model.ResponseDataUserItem
import com.fal.challengechapter6.network.ApiService
import com.fal.challengechapter6.repository.FirebaseRepository
import com.fal.challengechapter6.repository.UserPrefRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val api: ApiService,
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuth: FirebaseAuth,
    application: Application
) : AndroidViewModel(application) {

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
    fun getUser(email: String, password: String){
        api.getUser()
            .enqueue(object : Callback<List<ResponseDataUserItem>>{
                override fun onResponse(
                    call: Call<List<ResponseDataUserItem>>,
                    response: Response<List<ResponseDataUserItem>>,
                ) {
                    if (response.isSuccessful){
                        userList.postValue(response.body())
                        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                            if (it.result?.signInMethods?.size == 0){
                                userList.postValue(null)
                            }else{
                                firebaseRepository.login(email, password).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        firebaseAuth.currentUser?.isEmailVerified?.let { verified ->
                                            if (verified){
                                                firebaseRepository.fetchUser().addOnCompleteListener { userTask ->
                                                    if (userTask.isSuccessful){
                                                        userTask.result?.documents?.forEach { it ->
                                                            if (it.data!![email] == email){
                                                                firebaseAuth.currentUser?.email!!
                                                            }
                                                        }
                                                    }else{
                                                        userList.postValue(null)
                                                    }
                                                }
                                            }else{
                                                error("Email is not verified, check your email")
                                            }
                                        }
                                    }else{
                                        userList.postValue(null)
                                    }
                                }
                            }
                        }
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
                        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                            if (it.result?.signInMethods?.size == 0){
                                firebaseRepository.register(email, password, username).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        firebaseAuth.currentUser?.sendEmailVerification()
                                        userData.postValue(response.body())
                                        firebaseRepository.saveUser(email, username, password).addOnCompleteListener { it ->
                                            if (it.isSuccessful){
                                                userData.postValue(response.body())
                                            }else{
                                                userData.postValue(null)
                                            }
                                        }
                                    }else{
                                        userData.postValue(null)
                                    }
                                }
                            }else{
                                userData.postValue(null)
                            }
                        }
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