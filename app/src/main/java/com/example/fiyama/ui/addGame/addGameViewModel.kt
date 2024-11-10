package com.example.fiyama.ui.addGame

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fiyama.data.DataRepository
import com.example.fiyama.data.gameRoom
import com.example.fiyama.data.user
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val addGametag = "addGameViewModel"

class addGameViewModel(
    private val dataRepository: DataRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    var uiState = MutableStateFlow(addGameUiState())
    private set

    init {
        val currentUser = savedStateHandle.get<String>("currentUser")
        uiState.update { it.copy(
            currentUsername = currentUser?:""
        ) }
    }

    fun searchChange(query:String){
        uiState.update { it.copy(
            searchQuery = query
        ) }
    }
    fun searchUser(){
        Log.d(addGametag,"searchUser called")
        uiState.update { it.copy(
            isLoading = true
        ) }
        viewModelScope.launch{
            val result = async{
                dataRepository.searchUsers(
                    username = uiState.value.currentUsername,
                    query = uiState.value.searchQuery,
                    onFailure = {
                        Log.e(addGametag, "searchUser error : $it ")
                        uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true
                            )
                        }
                    }
                )
            }
            Log.d(addGametag,"searchUser result : ${result.await()}")
            uiState.update { it.copy(
                chatUsers = result.await(),
                isLoading = false,
                searched = true)}
        }
    }

    fun toggleUser(checked:Boolean,username: String){
        if(checked){
            uiState.update { it.copy(
                newMembers = it.newMembers + user(username = username)
            ) }
        }
        else{
            uiState.update { it.copy(
                newMembers = it.newMembers - user(username = username)
            ) }
        }
    }

    fun resetSuccess(){
        uiState.update { it.copy(
            addGameSuccess = false)}
    }

    fun updateGameName(name:String){
        uiState.update { it.copy(
            gameName = name
        ) }
    }

    fun addGame(){
        Log.d(addGametag,"addGame called")
        viewModelScope.launch{
            dataRepository.addNewGameRoom(
                gameRoom = gameRoom(
                    roomName = uiState.value.gameName,
                    players = uiState.value.newMembers.map { it.username } + uiState.value.currentUsername,
                    godUsername = uiState.value.currentUsername
                ),
                onSuccess = {
                    Log.d(addGametag,"addGame success")
                    uiState.update { it.copy(
                        addGameSuccess = true
                    ) }
                },
                onFailure = {
                    Log.e(addGametag, "addGame error : $it ")
                    uiState.update {
                        it.copy(
                            isError = true
                        )}
                }
            )
        }
    }
}

data class addGameUiState(
    val currentUsername:String = "",
    val searchQuery:String = "",
    val newMembers:List<user> = listOf(),
    val isLoading:Boolean = false,
    val isError: Boolean = false,
    val searched: Boolean = false,
    val chatUsers:List<user> = listOf(),
    val gameName:String="",
    val addGameSuccess: Boolean = false
)