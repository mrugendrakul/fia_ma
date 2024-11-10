package com.example.fiyama.ui.currentGame

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fiyama.data.DataRepository
import com.example.fiyama.data.gamePlayer
import com.example.fiyama.data.gameRoom
import com.example.fiyama.data.playerRole
import com.example.fiyama.data.user
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class currentGameViewModel(
    private val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    var gameUiState = MutableStateFlow(currGameUiState())
        private set
    init {
        val currRoom = savedStateHandle.get<String>("roomId")
        gameUiState.update { it.copy(currentRoomId = currRoom.toString()) }
        beginGame()
    }
    fun beginGame(){
        viewModelScope.launch{
            val currRoom = async{
                dataRepository.getCurrentRoom(
                    roomId = gameUiState.value.currentRoomId,
                    onFailure = {
                        Log.e("currentGameViewModel", "beginGame:")
                    }
                )
            }
            val currUsr = async{
                dataRepository.getCurrentUser()
            }
            currRoom.await()
            currUsr.await()
            gameUiState.update { it.copy(
                currentRoom = currRoom.await(),
                currentGod = currRoom.await().godUsername,
                currentUser = currUsr.await()?: user()
            ) }
            if (gameUiState.value.currentUser.username==""){
                return@launch
            }
            dataRepository.liveGamePlayersRoles(
                roomId = gameUiState.value.currentRoom.gameId,
                onAdd = {addPlayer->
                    gameUiState.update { it.copy(
                        players = it.players + addPlayer)
                    }
                    if (addPlayer.role == playerRole.God){
                        gameUiState.update { it.copy(
                            currentGod = addPlayer.username
                        ) }
                    }
                },
                onModify = {playerModify->
                    gameUiState.update { it.copy(
                        players = it.players.map {
                            if(it.playerId == playerModify.playerId){
                                playerModify
                            }else{
                                it
                            }
                        }
                    ) }
                    if (playerModify.role == playerRole.God){
                        gameUiState.update { it.copy(
                            currentGod = playerModify.username
                        ) }
                    }                },
                onRemove = {removePlayer->
                    gameUiState.update { it.copy(
                        players = it.players - removePlayer)
                    }
                    if (removePlayer.role == playerRole.God){
                        gameUiState.update { it.copy(
                            currentGod = removePlayer.username
                        ) }
                    }
                },
                onFailure = {}
            )
        }
    }

    fun setCurrentRole(player: gamePlayer, role: playerRole){
        viewModelScope.launch{
            dataRepository.updatePlayerRole(
                roomId = gameUiState.value.currentRoomId,
                player = player,
                role = role,
                onError = {Log.e("currentGameViewModel", "setCurrentRole:failed : $it ")}
            )
            if (role == playerRole.God){
                val currentPlayer = gameUiState.value.players.find {it.username == gameUiState.value.currentUser.username}
                dataRepository.updatePlayerRole(
                    roomId = gameUiState.value.currentRoomId,
                    player = currentPlayer?: gamePlayer(),
                    role = playerRole.Unassigned,
                    onError = {Log.e("currentGameViewModel", "setCurrentRole:failed : $it ")}
                )
                gameUiState.update { it.copy(currentGod = player.username) }
            }
        }
    }

    fun resetRoles(){
        gameUiState.update { it.copy(
            showToast = true,
            toastMessage = "Reset Started"
        ) }
        viewModelScope.launch{
            gameUiState.value.players.filter { it.role != playerRole.God }.forEach {
                dataRepository.updatePlayerRole(
                    roomId = gameUiState.value.currentRoomId,
                    player = it,
                    role = playerRole.Unassigned,
                    onError = {Log.e("currentGameViewModel", "setCurrentRole:failed : $it ")}
                )
            }
        }
    }

    fun resetToast(){
        gameUiState.update { it.copy(
            showToast = false
        ) }
    }

    fun randomizeRoles(){

    }

    fun setTempGod(tempGod: gamePlayer){
        gameUiState.update { it.copy(tempGod = tempGod) }
    }

    fun updateMafiaCount(count:Int){
        gameUiState.update { it.copy(mafiaCount = count) }
    }
    fun updateDoctorCount(count:Int){
        gameUiState.update { it.copy(doctorCount = count) }
    }
    fun updatePoliceCount(count:Int){
        gameUiState.update { it.copy(policeCount = count) }
    }
}

data class currGameUiState(
    val currentRoomId:String = "",
    val currentRoom : gameRoom = gameRoom(),
    val players:List<gamePlayer> = listOf(),
    val isGod:Boolean = false,
    val currentUser: user = user(),
    val currentGod:String = "",
    val tempGod:gamePlayer = gamePlayer(),
    val mafiaCount: Int = 1,
    val doctorCount: Int = 1,
    val policeCount: Int = 1,
    val showToast:Boolean = false,
    val toastMessage:String = ""
)