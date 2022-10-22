package com.fal.challengechapter6.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.network.ApiClient
import com.fal.challengechapter6.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val apiService: ApiService): ViewModel() {

    var allData : MutableLiveData<List<ResponseDataTaskItem>?> = MutableLiveData()
    var updateData : MutableLiveData<ResponseDataTaskItem?> = MutableLiveData()
    var deleteData : MutableLiveData<String?> = MutableLiveData()

    fun allLiveData() : MutableLiveData<List<ResponseDataTaskItem>?>{
        return allData
    }

    fun updateLiveData() : MutableLiveData<ResponseDataTaskItem?>{
        return updateData
    }

    fun deleteLiveData() : MutableLiveData<String?>{
        return deleteData
    }

    //Retrofit
    fun callAllData(userId: String){
        apiService.getTask(userId)
            .enqueue(object : Callback<List<ResponseDataTaskItem>>{
                override fun onResponse(
                    call: Call<List<ResponseDataTaskItem>>,
                    response: Response<List<ResponseDataTaskItem>>,
                ) {
                    if (response.isSuccessful){
                        allData.postValue(response.body())
                    }else{
                        allData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<List<ResponseDataTaskItem>>, t: Throwable) {
                    allData.postValue(null)
                }

            })
    }

    fun callUpdateData(category : String, content : String, idTask : String, image : String, title : String, userId : String){
        apiService.putData(userId, idTask, ResponseDataTaskItem(category, content, idTask, image, title, userId))
            .enqueue(object : Callback<ResponseDataTaskItem>{
                override fun onResponse(
                    call: Call<ResponseDataTaskItem>,
                    response: Response<ResponseDataTaskItem>,
                ) {
                    if (response.isSuccessful){
                        updateData.postValue(response.body())
                    }else{
                        updateData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<ResponseDataTaskItem>, t: Throwable) {
                    updateData.postValue(null)
                }

            })
    }

    fun callDeleteData(userId: String, idTask: String){
        apiService.delData(userId, idTask)
            .enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful){
                        deleteData.postValue(response.body())
                    }else{
                        deleteData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    deleteData.postValue(null)
                }

            })
    }
}