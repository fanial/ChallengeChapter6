package com.fal.challengechapter6.repository

import com.fal.challengechapter6.model.firebase.FirebaseSource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val firebaseSource: FirebaseSource, ) {
    fun register(email: String, password: String, username: String) = firebaseSource.register(email, password, username)

    fun login(email: String, password: String) = firebaseSource.login(email, password)

    fun saveUser(email: String, username: String, password: String) = firebaseSource.saveUser(email, username, password)

    fun fetchUser() = firebaseSource.fetchUser()

//    fun sendForgotPass(email: String)=firebaseSource.sendForgotPass(email)

}