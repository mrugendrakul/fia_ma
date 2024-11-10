package com.example.fiyama

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fiyama.data.gamePlayer
import com.example.fiyama.data.gameRoom
import com.example.fiyama.data.playerRole
import com.example.fiyama.data.user
import com.example.fiyama.network.FirebaseNetworkApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.test.runTest
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var firebaseNetworkApi: FirebaseNetworkApi
    @Before
    fun initTests(){
        val db = Firebase.firestore
        firebaseNetworkApi = FirebaseNetworkApi(
            usersCollection = db.collection("users"),
            gameRoomCollection = db.collection("gameRooms")
        )
    }
    @After
    fun TearDown(){
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.fiyama", appContext.packageName)
    }

    @Test
    fun addUsers_returnSuccess() =runTest{
        firebaseNetworkApi.addUser(
            user = user(id="124", username = "someone")
        )
    }

    @Test
    fun networkApi_addGame_returnSuccess() =
    runTest(){
        firebaseNetworkApi.createGameRoom(
            gameRoom = gameRoom(
                roomName = "Testing room new",
                players = listOf("user3", "user4")
            ),

            onFailure = {
                Log.e("TestLogs" , "Failed with error : $it")
                assertTrue(false)},
            onError = {}
        )
    }

    @Test
    fun networkApi_addGame_returnFailure() =
        runTest(){
            firebaseNetworkApi.changePlayerRole(
                roomId = "6q2hcMNxHUz1HtAbiALI",
                player = gamePlayer(
                    playerId = "25Pd3qOLjtdn3aAsbNd1",
                    role = playerRole.Unassigned,
                    username = "user4"
                ),
                role = playerRole.God,
                onError = {Log.e("TestLogs" , "Failed with error : $it")
                    assertTrue(false)}
            )
        }
}