package com.example.fiyama.ui.startHere

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fiyama.data.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class welViewmodel(
    private val dataRepo: DataRepository

): ViewModel() {
    private val welLog = "WelcomeviewModelLogs"

    var welState = MutableStateFlow(welState())
        private set


    fun updateUsername(username: String) {
        welState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        welState.update { it.copy(password = password) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        welState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun singupButton() {
        welState.update { it.copy(isLoading = true) }
        if (welState.value.password != welState.value.confirmPassword){
            welState.update { it.copy(
                isLoading = false,
                isError = true,
                errorMessage = "Passwords do not match"
            ) }
            return
        }
        viewModelScope.launch {
            delay(2000)
            try{
                dataRepo.signUpUser(
                    email = welState.value.username + "@mafiya.com",
                    password = welState.value.password,
                    onSuccess = {
                        welState.update { it.copy(
                            successSignal = true,
                            isLoading = false
                        ) }
                    },
                    onFailure = {error->
                        Log.e(welLog,"Exception in loginButton():$error")
                        welState.update { it.copy(
                            successSignal = false,
                            isLoading = false,
                            isError = true,
                            errorMessage =error
                        ) }
                    }
                )
            }
            catch (e: Exception){
                Log.e(welLog,"Exception in loginButton() in catch:$e")
                welState.update { it.copy(
                    successSignal = false,
                    isLoading = false,
                    isError = true,
                    errorMessage = it.toString()
                ) }
            }

        }
    }

    fun loginButton() {
        welState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            delay(2000)
            try{
                dataRepo.loginUser(
                    email = welState.value.username + "@mafiya.com",
                    password = welState.value.password,
                    onSuccess = {
                        welState.update { it.copy(
                            successSignal = true,
                            isLoading = false
                        ) }
                    },
                    onFailure = {error->
                        Log.e(welLog,"Exception in loginButton():$error")
                        welState.update { it.copy(
                            successSignal = false,
                            isLoading = false,
                            isError = true,
                            errorMessage =error
                        ) }
                    }
                )
            }
            catch (e: Exception){
                Log.e(welLog,"Exception in loginButton() in catch:$e")
                welState.update { it.copy(
                    successSignal = false,
                    isLoading = false,
                    isError = true,
                    errorMessage = it.toString()
                ) }
            }

        }
    }

    fun resetSuccessSignal() {
        welState.update {
            it.copy(successSignal = false)
        }
    }
}

data class welState(
    val username:String="",
    val password:String="",
    val confirmPassword:String="",
    val successSignal: Boolean=false,
    val isLoading: Boolean=false,
    val isError: Boolean = false,
    val errorMessage: String= ""
)