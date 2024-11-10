package com.example.fiyama

import com.example.fiyama.authentication.Authentication
import com.example.fiyama.authentication.FirebaseAuthentication
import com.example.fiyama.data.ApplicationDataRepository
import com.example.fiyama.data.DataRepository
import com.example.fiyama.network.FirebaseNetworkApi
import com.example.fiyama.network.NetworkApi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

interface AppContainer {
    val authentication: Authentication
    val dataRepository: DataRepository
    val networkApi: NetworkApi
}

class DefaultAppContainer(private val context: MyApplication): AppContainer{
    override val authentication: Authentication = FirebaseAuthentication(auth = Firebase.auth)
    private val db = Firebase.firestore
    override val networkApi: NetworkApi = FirebaseNetworkApi(
        usersCollection = db.collection("users"),
        gameRoomCollection = db.collection("gameRooms")
    )
    override val dataRepository: DataRepository = ApplicationDataRepository(
        authentication = authentication,
        networkApi =  networkApi
    )
}