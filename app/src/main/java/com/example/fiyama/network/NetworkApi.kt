package com.example.fiyama.network

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.fiyama.data.gamePlayer
import com.example.fiyama.data.gameRoom
import com.example.fiyama.data.playerRole
import com.example.fiyama.data.user
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

interface NetworkApi {
    suspend fun addUser(
        user: user
    )

    suspend fun getLiveGameRooms(
        username: String,
        onAdd: (gameRoom) -> Unit,
        onRemove: (gameRoom) -> Unit,
        onFailure: (String) -> Unit,
        onModify: (gameRoom) -> Unit
    )

    suspend fun createGameRoom(
        gameRoom: gameRoom,
        onFailure: (String) -> Unit,
        onError: (String) -> Unit
    )

    suspend fun closeGameRoomsListner()

    suspend fun getSearchUsersResults(query: String): List<user>

    suspend fun livePlayersRoles(
        roomId: String,
        onAdd: (gamePlayer) -> Unit,
        onModify: (gamePlayer) -> Unit,
        onRemove: (gamePlayer) -> Unit,
        onFailure: (String) -> Unit
    )

    suspend fun closeLivePlayersRoles()

    suspend fun changePlayerRole(
        roomId: String,
        player: gamePlayer,
        role: playerRole,
        onError: (String) -> Unit
    )

    suspend fun getCurrentRoom(
        roomId: String,
        onFailure: (String) -> Unit
    ):gameRoom
}

val networkTag = "FirebaseNetworkApi"

class FirebaseNetworkApi(
    private val usersCollection: CollectionReference,
    private val gameRoomCollection: CollectionReference
) : NetworkApi {
    override suspend fun addUser(user: user) {
        try {
            usersCollection.document(user.id).set(user).await()
        } catch (e: Exception) {
            Log.e(networkTag, "addUser: ", e)
        }
    }

    private var listenRooms: ListenerRegistration? = null


    override suspend fun closeGameRoomsListner() {
        listenRooms?.remove()
    }

    override suspend fun getLiveGameRooms(
        username: String,
        onAdd: (gameRoom) -> Unit,
        onRemove: (gameRoom) -> Unit,
        onFailure: (String) -> Unit,
        onModify: (gameRoom) -> Unit
    ) {
        listenRooms = null
        listenRooms = gameRoomCollection
            .whereArrayContains("players", username)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    {
                        Log.e(networkTag, "getLiveGameRooms: ", e)
                        onFailure(e.message.toString())
                        return@addSnapshotListener
                    }
                }

                for (doc in snapshot!!.documentChanges) {
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(networkTag, "getLiveGameRooms: ${doc.document.data}")
                            val gameRoomAdd = gameRoom(
                                roomName = doc.document.data["roomName"].toString(),
                                players = doc.document.data["players"] as List<String>,
                                godUsername = doc.document.data["godUsername"].toString(),
                                gameId = doc.document.id
                            )
                            onAdd(gameRoomAdd)
                        }

                        DocumentChange.Type.MODIFIED -> {
                            Log.d(networkTag, "getLiveGameRooms Modify: ${doc.document.data}")
                            val gameRoomAdd = gameRoom(
                                roomName = doc.document.data["roomName"].toString(),
                                players = doc.document.data["players"] as List<String>,
                                godUsername = doc.document.data["godUsername"].toString(),
                                gameId = doc.document.id
                            )
                            onModify(gameRoomAdd)
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(networkTag, "getLiveGameRooms: ${doc.document.data}")
                            val gameRoomRemove = gameRoom(
                                roomName = doc.document.data["roomName"].toString(),
                                players = doc.document.data["players"] as List<String>,
                                godUsername = doc.document.data["godUsername"].toString(),
                                gameId = doc.document.id
                            )
                            onRemove(gameRoomRemove)
                        }
                    }
                }
            }
    }

    override suspend fun createGameRoom(
        gameRoom: gameRoom,
        onFailure: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val newGameRoom = gameRoomCollection.add(gameRoom).await()
            val players = gameRoom.players.map { player ->
                gamePlayer(username = player)
            }
            players.forEach { player ->
                gameRoomCollection.document(newGameRoom.id).collection("players").add(
                    player
                )
            }
        } catch (e: Exception) {
            Log.e(networkTag, "createGameRoom: ", e)
            onFailure(e.message.toString())
        }
    }

    override suspend fun getSearchUsersResults(query: String): List<user> {
        Log.d(networkTag, "getSearchUsersResults: $query")
        val users = mutableListOf<user>()
        try {
            val allUsers = usersCollection
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo("username", query + "\uf8ff")
                .limit(10)
                .get()
            for (doc in allUsers.await().documents) {
                val fcmToken = doc.getString("fcmToken") ?: ""

                val profilePic = doc.getString("profielPic") ?: ""
                val usern = doc.getString("username") ?: ""

//            val docId = doc.id
//            val user = User(fcmToken, profilePic, "uniqueId", usern, docId)
                val newuser = user(
                    username = usern,
//                    fcmToken = fcmToken
                )

                Log.d(networkTag, "Got user with id : ${newuser.username}")
                users.add(newuser)
            }
        } catch (e: Exception) {
            Log.e(networkTag, "getSearchUsersResults: ", e)
            throw e
        }
        return users
    }

    private var playerListner: ListenerRegistration? = null
    override suspend fun livePlayersRoles(
        roomId: String,
        onAdd: (gamePlayer) -> Unit,
        onModify: (gamePlayer) -> Unit,
        onRemove: (gamePlayer) -> Unit,
        onFailure: (String) -> Unit
    ) {
        playerListner = null
        playerListner = gameRoomCollection
            .document(roomId)
            .collection("players")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    {
                        Log.e(networkTag, "getLiveGameRooms: ", e)
                        onFailure(e.message.toString())
                        return@addSnapshotListener
                    }
                }

                for (doc in snapshot!!.documentChanges) {
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(networkTag, "getLiveGameRooms: ${doc.document.data}")
                            val role = doc.document.data["role"]
                            val plarRole = when (role) {
                                "God" -> playerRole.God
                                "Police" -> playerRole.Police
                                "Mafia" -> playerRole.Mafia
                                "Doctor" -> playerRole.Doctor
                                "Villager" -> playerRole.Villager
                                "Unassigned" -> playerRole.Unassigned
                                else-> playerRole.Unassigned
                            }
                            val gameRoomAdd = gamePlayer(
                                username = doc.document.data["username"].toString(),
                                role = plarRole,
                                playerId = doc.document.id,
                            )
                            onAdd(gameRoomAdd)
                        }

                        DocumentChange.Type.MODIFIED -> {
                            Log.d(networkTag, "getLiveGameRooms: ${doc.document.data}")
                            val role = doc.document.data["role"]
                            val plarRole = when (role) {
                                "God" -> playerRole.God
                                "Police" -> playerRole.Police
                                "Mafia" -> playerRole.Mafia
                                "Doctor" -> playerRole.Doctor
                                "Villager" -> playerRole.Villager
                                "Unassigned" -> playerRole.Unassigned
                                else-> playerRole.Unassigned
                            }
                            val gameRoomRemove = gamePlayer(
                                username = doc.document.data["username"].toString(),
                                role = plarRole,
                                playerId = doc.document.id,
                            )
                            onModify(gameRoomRemove)
                        }

                        DocumentChange.Type.REMOVED -> {
                            Log.d(networkTag, "getLiveGameRooms: ${doc.document.data}")
                            val role = doc.document.data["role"]
                            val plarRole = when (role) {
                                "God" -> playerRole.God
                                "Police" -> playerRole.Police
                                "Mafia" -> playerRole.Mafia
                                "Doctor" -> playerRole.Doctor
                                "Villager" -> playerRole.Villager
                                "Unassigned" -> playerRole.Unassigned
                                else-> playerRole.Unassigned
                            }
                            val gameRoomRemove = gamePlayer(
                                username = doc.document.data["username"].toString(),
                                role = plarRole,
                                playerId = doc.document.id,
                            )
                            onRemove(gameRoomRemove)
                        }
                    }
                }
            }
    }

    override suspend fun closeLivePlayersRoles() {
        playerListner?.remove()
    }

    override suspend fun changePlayerRole(
        roomId: String,
        player: gamePlayer,
        role: playerRole,
        onError: (String) -> Unit
    ) {
        try {
            if (role == playerRole.God){
                gameRoomCollection.document(roomId)
                    .update("godUsername", player.username)
                    .await()
            }
            gameRoomCollection
                .document(roomId)
                .collection("players")
                .document(player.playerId)
                .set(player.copy(role = role))
                .await()
        } catch (e: Exception) {
            Log.e(networkTag, "Error setting the data: ${e.message}")
            onError(e.message.toString())
        }
    }

    override suspend fun getCurrentRoom(roomId: String, onFailure: (String) -> Unit): gameRoom {
        val gameRoom = gameRoomCollection.document(roomId).get().await()
        val godUsername = gameRoom.get("godUsername") as String
        val players = gameRoom.get("players") as List<String>
        val roomName = gameRoom.get("roomName") as String
        return gameRoom(
            gameId = roomId,
            godUsername = godUsername,
            players = players,
            roomName = roomName)
    }
}
