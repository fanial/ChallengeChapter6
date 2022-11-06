package com.fal.challengechapter6.model.firebase

import com.fal.challengechapter6.User
import com.fal.challengechapter6.model.ResponseDataUserItem
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseSource @Inject constructor(private val firebaseAuth : FirebaseAuth, private val firestore: FirebaseFirestore) {
    //Register User using Email and Password
    fun register(email : String, password : String, username : String) = firebaseAuth.createUserWithEmailAndPassword(email, password)

    //Login User using Email and Password
    fun login(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email, password)

    //Save User
    fun saveUser(email: String, username: String, password: String) = firestore.collection("users").document(email).set(ResponseDataUserItem(email, "",username, password))

    //Fetch User
    fun fetchUser()=firestore.collection("users").get()

//    //forgot password
//    fun sendForgotPass(email: String) = firebaseAuth.sendPasswordResetEmail(email)
}