package com.example.fiyama.ui.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiyama.data.gameRoom
import com.example.fiyama.ui.ApptopBar
import com.example.fiyama.ui.GodViewmodelProvider
import com.example.fiyama.ui.destination
import com.example.fiyama.ui.theme.FiyamaTheme
import com.google.firebase.Timestamp
import com.example.fiyama.R

object gameScreen : destination {
    override val route = "game"
    override val title = "Game"
    override val canGoBack: Boolean = false

}

@Composable
fun GameStartScreen(
    gameViewModel: gameViewModel = viewModel(factory = GodViewmodelProvider.Factory),
    navigateToAddChat: (String) -> Unit,
    navigateToGame: (String) -> Unit,
    navigateToWelcome:()->Unit
) {
    val uiState = gameViewModel.gameUiState.collectAsState().value
    if(uiState.logoutSuccess){
        navigateToWelcome()
        gameViewModel.resetLogoutSuccess()
    }
    GameScreenBody(
        uiState = uiState,
        navigateToAddChat = navigateToAddChat,
        navigateToGame = navigateToGame,
        logout = { gameViewModel.logout()}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenBody(
    uiState: GameUiState,
    navigateToAddChat: (String) -> Unit,
    navigateToGame: (String) -> Unit,
    logout:()->Unit
) {
    Scaffold(topBar = {
        Column() {
            ApptopBar(
                destinationData = gameScreen,

                navigateUp = { },
                action = {
                    IconButton(onClick = {logout()}) {
                        Icon(painter = painterResource(R.drawable.logout),
                            contentDescription = "Logout")
                    }
                }
            )
            Text(text = "You are signed in as ${uiState.currentUsername}")

        }
    }, floatingActionButton = {
        AddChatFab(navigateToAddChat = {
            navigateToAddChat(uiState.currentUsername)
        }, text = "New Room")
    }) { paddingValues ->

        if (uiState.gameRooms.isEmpty()) {
            Row(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ) {
//                Text(text = "You are signed in as ${uiState.currentUsername} , ")
                Text(
                    text = "Create New Game", textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(items = uiState.gameRooms) { gameRoom ->
                    SingleGameRoom(
                        gameRoom = gameRoom,
                        cardModifier = Modifier
                            .padding(5.dp)
                            .clickable(enabled = true){
                                navigateToGame(gameRoom.gameId)
                            },
                        isCardSelected = false
                    )
                }

            }
        }
    }
}

@Composable
fun AddChatFab(
    navigateToAddChat: () -> Unit,
    text: String
) {
    ExtendedFloatingActionButton(
        onClick = { navigateToAddChat() },
        icon = {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = "New Room"
            )
        },
        text = { Text(text = text) },
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
}

@Composable
fun SingleGameRoom(
    gameRoom: gameRoom, cardModifier: Modifier, isCardSelected: Boolean
) {
    var expanded by remember {
        mutableStateOf(false)
    }
//    val lastMessageContent = chat.lastMessage.content.substring(20)
    ElevatedCard(
        modifier = cardModifier,
        shape = RoundedCornerShape(25.dp),
//        enabled = isCardEnabled,
        elevation = CardDefaults.elevatedCardElevation(0.dp), colors = if (isCardSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        } else {
            CardDefaults.cardColors(
//                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
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
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Room : ${gameRoom.roomName} ",
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(15.dp))
                Text(
                    text = "God : ${gameRoom.godUsername}",
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
//
    }
}

@Composable
@Preview
fun PreviewSingleGameRoom() {
    SingleGameRoom(
        gameRoom = gameRoom(
            roomName = "room1",
            players = listOf(),
        ), cardModifier = Modifier, isCardSelected = false
    )
}

@Composable
@Preview(apiLevel = 34)
fun PreviewGameStartScreen() {
    FiyamaTheme() {
        GameScreenBody(
            uiState = GameUiState(
                currentUsername = "Testing 123",
                gameRooms = listOf(
                    gameRoom(
                        roomName = "room1",
                        players = listOf(),
                    ),
                    gameRoom(
                        roomName = "room1",
                        players = listOf(),
                    )
                )
            ),
            navigateToAddChat = {},
            navigateToGame = {},
            logout = {}
        )
    }
}