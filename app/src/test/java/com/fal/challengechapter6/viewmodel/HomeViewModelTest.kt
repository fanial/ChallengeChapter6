package com.fal.challengechapter6.viewmodel

import com.fal.challengechapter6.model.ResponseDataTaskItem
import com.fal.challengechapter6.network.ApiService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import retrofit2.Call

class HomeViewModelTest {
    private lateinit var service: ApiService

    @Before
    fun setUp() {
        service = mockk()
    }

    @Test
    fun callAllData(): Unit = runBlocking {
        val response = mockk<Call<List<ResponseDataTaskItem>>>()
        every {
            service.getTask("1")
        }returns response

        val result = service.getTask("1")

        verify {
            service.getTask("1")
        }
        assertEquals(result, response)
    }

    @Test
    fun callAllFavorite(): Unit = runBlocking {
        val response = mockk<Call<List<ResponseDataTaskItem>>>()
        every {
            service.getFav(
                "1",
                "favorite"
            )
        }returns response

        val result = service.getFav(
            "1",
            "favorite"
        )

        verify {
            service.getFav(
                "1",
                "favorite"
            )
        }

        assertEquals(result, response)
    }
}