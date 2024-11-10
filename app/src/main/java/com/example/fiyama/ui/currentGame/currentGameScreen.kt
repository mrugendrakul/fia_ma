package com.example.fiyama.ui.currentGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiyama.data.gamePlayer
import com.example.fiyama.data.gameRoom
import com.example.fiyama.data.playerRole
import com.example.fiyama.ui.ApptopBar
import com.example.fiyama.ui.GodViewmodelProvider
import com.example.fiyama.ui.destination
import com.example.fiyama.ui.theme.FiyamaTheme
import com.example.fiyama.R
import com.example.fiyama.data.user

object gameRoomDestination : destination{
    override val route = "game_room"
    override val title: String = "Your game"
    override val canGoBack: Boolean= true
}

@Composable
fun GameRoomScreen(
    viewModel: currentGameViewModel = viewModel(factory = GodViewmodelProvider.Factory),
    navigateUp: () -> Unit
){
    val gameUiState = viewModel.gameUiState.collectAsState().value
//    Text(text = "current gameId : ${gameUiState.currentRoomId}")
    GameRoomBody(
        navigateUp = navigateUp,
        uiState = gameUiState,
        setRole = {gamePlayer,playerRole->viewModel.setCurrentRole(player = gamePlayer, role = playerRole)},
        setTempGod = {viewModel.setTempGod(it)}
    )
}

@Composable
fun ChangeGodConfirmDialog(dismissDialog:()->Unit,player: gamePlayer,setRole: (gamePlayer,playerRole)->Unit){
        AlertDialog(
            onDismissRequest = { dismissDialog },
            confirmButton = {
                Button(
                    onClick = {
                        setRole(player,playerRole.God)
                        dismissDialog()
                    }
                ) {
                    Text("Confirm")
                }
            },
            title = {Text(text = "Make someone else God?")},
            text = {Text (text = "After confirming you cannot make yourself God again")},
            dismissButton = {OutlinedButton(onClick = dismissDialog){
                Text("No")
            }
            }
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRoomBody(
    navigateUp :()->Unit,
    uiState: currGameUiState,
    setRole: (gamePlayer,playerRole)->Unit,
    setTempGod:(gamePlayer)->Unit
){
    var dialogVisible by remember {
        mutableStateOf(false)
    }
    if (dialogVisible){
        ChangeGodConfirmDialog(dismissDialog = {dialogVisible = false}, player = uiState.tempGod, setRole = setRole)
    }

    Scaffold(
        topBar = {
            ApptopBar(
                destinationData = gameRoomDestination,
                navigateUp = navigateUp,
                title = uiState.currentRoom.roomName
            )
        },
        bottomBar = {
            if (uiState.currentUser.username == uiState.currentGod) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),

                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        onClick = {}
                    ) { Text("Reset All Roles") }
                }
            }
        }
    ) {
        if (uiState.currentUser.username == uiState.currentGod){

            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item{
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Players Information for God",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                items(items = uiState.players - uiState.players.filter { it.username == uiState.currentGod }) { player ->
                    SinglePlayerInfoForGod(
                        gamePlayer = player,
                        cardModifier = Modifier
                            .padding(5.dp),
                        setRole = setRole,
                        setGod = {
                            setTempGod(it)
                            dialogVisible = true
                        }
                    )
                }
            }
        }else{
            val newList  = uiState.players - uiState.players.filter { it.username == uiState.currentGod }
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item{
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Players Information for Mortals",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                items(items =newList
                    .map { if (it.username == uiState.currentUser.username){
                        it
                    }
                    else{
                        it.copy(
                            role = playerRole.NotVisible
                        )
                    }
                    }
                ) { player ->
                    SinglePlayerInfoForMortals(
                        gamePlayer = player,
                        cardModifier = Modifier
                            .padding(5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SinglePlayerInfoForMortals(
    gamePlayer: gamePlayer,cardModifier: Modifier,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
//    val lastMessageContent = chat.lastMessage.content.substring(20)
    ElevatedCard(
        modifier = cardModifier,
        shape = RoundedCornerShape(25.dp),
//        enabled = isCardEnabled,
        elevation = CardDefaults.elevatedCardElevation(0.dp), colors =
        CardDefaults.cardColors(
//                containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

//                border = BorderStroke(5.dp,MaterialTheme.colorScheme.primary)

    ) {
//        Switch(checked = selectedStatus, onCheckedChange = {
//            addToSelection(it, chat)
//        })
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {


//            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Player : ${gamePlayer.username.removeSuffix("@mafiya.com")} ",
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(15.dp))
                    Text(
                        text = "Role : ${gamePlayer.role}",
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
//                IconButton(onClick = {}) {
//                    Icon(
//                        painter = painterResource(R.drawable.expand_circle_down)
//                        , contentDescription = "arrow down"
//                    )
//                }
            }
        }
//
    }
}

@Composable
fun SinglePlayerInfoForGod(
    gamePlayer: gamePlayer,cardModifier: Modifier,
    setRole: (gamePlayer ,playerRole)->Unit,
    setGod:(gamePlayer)->Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    val rolesList = listOf(
        playerRole.Unassigned,
        playerRole.Villager,
        playerRole.Police,
        playerRole.Doctor,
        playerRole.Mafia,
    )
//    val lastMessageContent = chat.lastMessage.content.substring(20)
    ElevatedCard(
        modifier = cardModifier,
        shape = RoundedCornerShape(25.dp),
//        enabled = isCardEnabled,
        elevation = CardDefaults.elevatedCardElevation(0.dp), colors =
            CardDefaults.cardColors(
//                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            )

//                border = BorderStroke(5.dp,MaterialTheme.colorScheme.primary)

    ) {
        DropdownMenu(expanded = expanded,
            onDismissRequest = {expanded = !expanded})
        {
            rolesList.forEach{role->
                DropdownMenuItem(text = { Text(text = role.toString()) },
                    onClick = {
                        setRole(gamePlayer,role)
                        expanded = false
                    })
            }
            DropdownMenuItem(text = { Text(text = playerRole.God.toString()) },
                onClick = {
                    setGod(gamePlayer)
                })
        }
//        Switch(checked = selectedStatus, onCheckedChange = {
//            addToSelection(it, chat)
//        })
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {


//            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Player : ${gamePlayer.username.removeSuffix("@mafiya.com")} ",
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(15.dp))
                    Text(
                        text = "Role : ${gamePlayer.role}",
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.expand_circle_down)
                        , contentDescription = "arrow down"
                    )
                }
            }
        }
//
    }
}

@Composable
@Preview
fun GameRoomPreview(){
    FiyamaTheme {
        GameRoomBody(
            navigateUp = {},
            uiState = currGameUiState(
                players = listOf(
                    gamePlayer(
                        username = "Test3",
                        role = playerRole.Unassigned
                    ),
                    gamePlayer(
                        username = "Test2",
                        role = playerRole.Unassigned
                    ),
                    gamePlayer(
                        username = "Test1",
                        role = playerRole.Police
                    ),
                    gamePlayer(
                        username = "nome",
                        role = playerRole.Villager
                    ),
                    gamePlayer(
                        username = "meself",
                        role = playerRole.God
                    ),
                ),
                currentRoom = gameRoom(
                    godUsername = "meself"
                ),
                currentUser = user(
                    username = "meself"
                ),
                currentGod = "meself"
            ),
            setRole = {gamePlayer,playerRole->},
            setTempGod = {}
        )
    }
}