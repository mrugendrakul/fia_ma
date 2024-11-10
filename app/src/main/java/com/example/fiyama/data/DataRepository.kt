package com.example.fiyama.data

import android.util.Log
import com.example.fiyama.authentication.Authentication
import com.example.fiyama.authentication.FirebaseAuthentication
import com.example.fiyama.network.NetworkApi
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

interface DataRepository {
    suspend fun loginUser(
        email: String,
        password: String,
        onSuccess: (user) -> Unit,
        onFailure: (String) -> Unit
    )

    suspend fun signUpUser(
        email: String,
        password: String,
        onSuccess: (user) -> Unit,
        onFailure: (String) -> Unit
    )

    suspend fun logoutUser(
        onSuccess: (user) -> Unit,
        onFailure: (String) -> Unit
    )

    suspend fun addNewGameRoom(
        gameRoom: gameRoom,
        onSuccess: (gameRoom) -> Unit,
        onFailure: (String) -> Unit
    )

    suspend fun getLiveGameRooms(
        username: String,
        onAdd: (gameRoom) -> Unit,
        onRemove: (gameRoom) -> Unit,
        onFailure: (String) -> Unit,
        onModify: (gameRoom) -> Unit
    )
    suspend fun removeLiveGameRoom()

    fun getCurrentUser(
    ): user?

    suspend fun searchUsers(
        username: String,
        query: String,
        onFailure: (String) -> Unit
    ): List<user>

    suspend fun liveGamePlayersRoles(
        roomId: String,
        onAdd: (gamePlayer) -> Unit,
        onModify:(gamePlayer)-> Unit,
        onRemove: (gamePlayer) -> Unit,
        onFailure: (String) -> Unit
    )

    suspend fun closeLivegame()

    suspend fun updatePlayerRole(
        roomId: String,
        player: gamePlayer,
        role: playerRole,
        onError:(String)->Unit
    )

    suspend fun getCurrentRoom(
        roomId: String,
        onFailure: (String) -> Unit
    ):gameRoom
}

val dataLogTag = "DataRepositoryLogs"

class ApplicationDataRepository(
    private val authentication: Authentication,
    private val networkApi: NetworkApi
) : DataRepository {
    override suspend fun loginUser(
        email: String,
        password: String,
        onSuccess: (user) -> Unit,
        onFailure: (String) -> Unit
    ) {
        authentication.loginUser(
            email,
            password,
            onSuccess = { logUser ->
                Log.d(dataLogTag, "Login Success")
                val currentUser = user(
                    username = logUser?.email.toString(),
                    id = logUser?.uid.toString()
                )
                onSuccess(currentUser)
            },
            onFailure
        )
    }

    override suspend fun signUpUser(
        email: String,
        password: String,
        onSuccess: (user) -> Unit,
        onFailure: (String) -> Unit
    ) {
        authentication.signUpUser(
            email,
            password,
            onSuccess = { logUser ->
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(dataLogTag, "SignUp Success")
                    val currentUser = user(
                        username = logUser?.email.toString(),
                        id = logUser?.uid.toString()
                    )
                    networkApi.addUser(currentUser)
                    onSuccess(currentUser)
                }
            },
            onFailure
        )
    }

    override suspend fun logoutUser(
        onSuccess: (user) -> Unit,
        onFailure: (String) -> Unit
    ) {
        authentication.logoutUser(
            onSuccess = {
                Log.d(dataLogTag, "Logout Success")
                val currentUser = user(
                    username = "logoutSuccess"
                )
                onSuccess(currentUser)
            },
            onFailure = onFailure
        )
    }

    override suspend fun addNewGameRoom(
        gameRoom: gameRoom,
        onSuccess: (gameRoom) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            networkApi.createGameRoom(
                gameRoom = gameRoom,
                onFailure = onFailure,
                onError = onFailure
            )
            onSuccess(gameRoom)
        } catch (e: Exception) {
            onFailure(e.message.toString())
        }
    }

    override suspend fun getLiveGameRooms(
        username: String,
        onAdd: (gameRoom) -> Unit,
        onRemove: (gameRoom) -> Unit,
        onFailure: (String) -> Unit,
        onModify: (gameRoom) -> Unit
    ) {
        try {
            networkApi.getLiveGameRooms(
                username = username,
                onAdd = onAdd,
                onRemove = onRemove,
                onFailure = onFailure,
                onModify = onModify
            )
        } catch (e: Exception) {
            onFailure(e.message.toString())
        }
    }

    override fun getCurrentUser(): user? {
        val currentUser = authentication.getCurrentUser()
        return user(
            username = currentUser?.email.toString(),
            id = currentUser?.uid.toString()
        )
    }

    override suspend fun searchUsers(
        username: String,
        query: String,
        onFailure: (String) -> Unit
    ): List<user> {
        Log.d(dataLogTag, "Search Query: $query")
        try {
            val users: List<user> =
                coroutineScope {
                    val res = async { networkApi.getSearchUsersResults(query) }
                    res.await()
                }
            Log.d(dataLogTag, "Search Success ${users}")
            return users.distinctBy { it.username }.sortedBy { it.username.lowercase() }
                .filter { it.username != username }
        } catch (e: Exception) {
            onFailure(e.message.toString())
            return emptyList()
        }
    }

    override suspend fun removeLiveGameRoom() {
        networkApi.closeGameRoomsListner()
    }

    override suspend fun liveGamePlayersRoles(
        roomId: String,
        onAdd: (gamePlayer) -> Unit,
        onModify: (gamePlayer) -> Unit,
        onRemove: (gamePlayer) -> Unit,
        onFailure: (String) -> Unit
    ) {
        networkApi.livePlayersRoles(
            roomId,
            onAdd,
            onModify,
            onRemove,
            onFailure
        )
    }

    override suspend fun closeLivegame() {
        networkApi.closeLivePlayersRoles()
    }

    override suspend fun updatePlayerRole(
        roomId: String,
        player: gamePlayer,
        role: playerRole,
        onError: (String) -> Unit
    ) {
        networkApi.changePlayerRole(
            roomId,
            player,
            role,
            onError
        )
    }

    override suspend fun getCurrentRoom(roomId: String,onFailure: (String) -> Unit): gameRoom {
        val currRoom:gameRoom = coroutineScope{val curr =
            async(){ networkApi.getCurrentRoom(roomId, onFailure = onFailure) }
        curr.await()
        }
        return currRoom
    }

}