package com.example.fiyama.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fiyama.MyApplication
import com.example.fiyama.ui.addGame.addGameViewModel
import com.example.fiyama.ui.currentGame.currentGameViewModel
import com.example.fiyama.ui.games.gameViewModel
import com.example.fiyama.ui.startHere.welViewmodel

object GodViewmodelProvider {
    val Factory = viewModelFactory {
        initializer {

            welViewmodel(
                dataRepo = myApplication().container.dataRepository
            )
        }
        initializer {
            gameViewModel(
                dataRepository = myApplication().container.dataRepository
            )
        }
        initializer {
            addGameViewModel(
                dataRepository = myApplication().container.dataRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        initializer {
            currentGameViewModel(
                dataRepository = myApplication().container.dataRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.myApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)