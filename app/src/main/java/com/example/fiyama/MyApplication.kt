package com.example.fiyama

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings

class MyApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        try{

            FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build()
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseLogs","initialized success")
        }
        catch (e:Exception){
            Log.d("FirebaseLogs","error in initializing")
        }

        if(BuildConfig.DEBUG){
            val firestore = Firebase.firestore
            firestore.useEmulator("10.0.2.2", 8080)

            firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }

            Firebase.auth.useEmulator("10.0.2.2", 9099)

        }


        container = DefaultAppContainer(context = this)


    }
}