package com.example.fiyama.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fiyama.data.DataRepository
import com.example.fiyama.data.gameRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class gameViewModel(
    private val dataRepository: DataRepository
): ViewModel() {
    var gameUiState = MutableStateFlow(GameUiState())
        private set
    init {
        startRoomListening()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(){
            dataRepository.removeLiveGameRoom()
        }
    }
    fun startRoomListening(){
        gameUiState.update { it.copy(
            isLoading = true
        ) }
        val currUser = dataRepository.getCurrentUser()
        gameUiState.update { it.copy(
            currentUsername = currUser?.username ?: ""
        ) }
        if (currUser?.username == ""){
            gameUiState.update { it.copy(
                isError = true,
                errorMessage = "User not found"
            ) }
            return
        }
        viewModelScope.launch{
            gameUiState.update { it.copy(
                isLoading = false
            ) }
            dataRepository.getLiveGameRooms(
                username = gameUiState.value.currentUsername,
                onAdd={newRoom->
                    gameUiState.update { it.copy(
                        gameRooms = it.gameRooms + newRoom
                    ) }
                },

                onRemove = {oldRoom->
                    gameUiState.update { it.copy(
                        gameRooms = it.gameRooms - oldRoom
                    ) }
                },
                onFailure = {
                    gameUiState.update { it.copy(
                        isError = true,
                        errorMessage = it.errorMessage
                    ) }
                },
                onModify = {
                    modifiedRoom->
                    gameUiState.update { it.copy(
                        gameRooms = it.gameRooms.map {
                            if (it.gameId == modifiedRoom.gameId){
                                modifiedRoom
                            }else{
                                it
                            }
                            })
                        }

                }
            )
        }
    }

    fun logout(){
        viewModelScope.launch(){
            dataRepository.logoutUser(
                onSuccess = {
                    gameUiState.update { it.copy(logoutSuccess = true) }
                },
                onFailure = {
                    gameUiState.update { it.copy(logoutSuccess = false) }
                }
            )
        }
    }
    fun resetLogoutSuccess(){
        gameUiState.update { it.copy(
            logoutSuccess = false
        ) }
    }
}

data class GameUiState(
    val currentUsername:String = "",
    val gameRooms:List<gameRoom> = listOf(),
    val isError: Boolean= false,
    val isLoading: Boolean = false,
    val errorMessage :String= "",
    val logoutSuccess: Boolean = false
)