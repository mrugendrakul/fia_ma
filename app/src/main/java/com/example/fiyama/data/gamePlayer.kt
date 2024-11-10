package com.example.fiyama.data

enum class playerRole(){
    God,
    Police,
    Mafia,
    Doctor,
    Villager,
    Unassigned,
    NotVisible
}

data class gamePlayer(
    val playerId: String = "",
    val username:String= "",
    val role: playerRole = playerRole.Unassigned
)