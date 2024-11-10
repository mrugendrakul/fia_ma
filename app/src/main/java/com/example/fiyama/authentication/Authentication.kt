package com.example.fiyama.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface Authentication {
    suspend fun loginUser(
        email:String,
        password:String,
        onSuccess:(FirebaseUser?)->Unit,
        onFailure:(String)->Unit
    )

    suspend fun signUpUser(
        email:String,
        password:String,
        onSuccess:(FirebaseUser?)->Unit,
        onFailure:(String)->Unit
    )

    suspend fun logoutUser(
        onSuccess:(FirebaseUser?)->Unit,
        onFailure:(String)->Unit
    )

    fun getCurrentUser():FirebaseUser?
}
val authLogTag = "FirebaseAuthentication"

class FirebaseAuthentication(
    private val auth:FirebaseAuth
) : Authentication{
    override suspend fun loginUser(
        email:String,
        password:String,
        onSuccess:(FirebaseUser?)->Unit,
        onFailure:(String)->Unit
    ) {
        Log.d(authLogTag, "loginUser: $email $password")
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {task->
                onSuccess(task.user)
            }
            .addOnFailureListener {
                Log.e(authLogTag, "loginUser: ${it.message}")
                onFailure(it.message.toString())
            }
    }

    override suspend fun signUpUser(
        email:String,
        password:String,
        onSuccess:(FirebaseUser?)->Unit,
        onFailure:(String)->Unit
    ) {
        Log.d(authLogTag, "signUpUser: $email $password")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess(it.user)
            }
            .addOnFailureListener {
                Log.e(authLogTag, "signUpUser: ${it.message}")
                onFailure(it.message.toString())
            }
    }

    override suspend fun logoutUser(
        onSuccess:(FirebaseUser?)->Unit,
        onFailure:(String)->Unit
    ) {
        try{
            auth.signOut()
            onSuccess(null)
        }catch(e:Exception){
            Log.e(authLogTag, "logoutUser: ${e.message}")
            onFailure(e.message.toString())
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}