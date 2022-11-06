package com.fal.challengechapter6.viewmodel

import com.fal.challengechapter6.model.ResponseDataUserItem
import com.fal.challengechapter6.model.firebase.FirebaseSource
import com.fal.challengechapter6.network.ApiService
import com.fal.challengechapter6.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Call

class UserViewModelTest {

    lateinit var service: ApiService

    @Before
    fun setUp() {
        service = mockk()
    }

    @Test
    fun getUser(): Unit = runBlocking {
        val response = mockk<Call<List<ResponseDataUserItem>>>()
        every {
            service.getUser()
        }returns response

        val result = service.getUser()

        verify {
            service.getUser()
        }
        assertEquals(result, response)
    }

    @Test
    fun postDataUser(): Unit = runBlocking{
        val responseData = mockk<Call<ResponseDataUserItem>>()

        every {
            service.postUser(
                ResponseDataUserItem("email@gmail.com","","password","dummy user")
            )
        }returns responseData

        val result = service.postUser(ResponseDataUserItem("email@gmail.com","","password","dummy user"))

        verify {
            service.postUser(
                ResponseDataUserItem("email@gmail.com","","password","dummy user")
            )
        }
        assertEquals(result, responseData)
    }

    @Test
    fun putUser(): Unit = runBlocking {
        val responseUpdate = mockk<Call<ResponseDataUserItem>>()
        every {
            service.putUser(
                userId = "1",
                ResponseDataUserItem("email@gmail.com","","password","dummy user")
            )
        }returns responseUpdate

        val result = service.putUser(
            userId = "1",
            ResponseDataUserItem("email@gmail.com","","password","dummy user")
        )

        verify {
            service.putUser(
                userId = "1",
                ResponseDataUserItem("email@gmail.com","","password","dummy user")
            )
        }
        assertEquals(result, responseUpdate)
    }
}