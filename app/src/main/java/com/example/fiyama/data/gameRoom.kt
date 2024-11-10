package com.example.fiyama.data

data class gameRoom(
    val gameId:String= "",
    val roomName:String="",
    val players:List<String> = listOf(),
    val godUsername:String = ""
)
