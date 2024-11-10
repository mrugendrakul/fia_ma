package com.example.fiyama.ui.addGame

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiyama.ui.ApptopBar
import com.example.fiyama.ui.destination
import com.example.fiyama.ui.theme.FiyamaTheme
import com.example.fiyama.R
import com.example.fiyama.data.user
import kotlin.math.sin

object addGameWithNameDestination: destination{
    override val route = "addGameWithName"
    override val title: String = "New Game"
    override val canGoBack: Boolean = true
}

@Composable
fun AddGameWithName(
    viewModel: addGameViewModel,
    navigateBack: () -> Unit,
    navigateSuccess:()->Unit
){
    val uiState = viewModel.uiState.collectAsState().value
    if (uiState.addGameSuccess){
        navigateSuccess()
        viewModel.resetSuccess()
    }
    AddGameNameBody(
        uiState = uiState,
        navigateBack,
        updateGameName = { viewModel.updateGameName(it) },
        addGame = {viewModel.addGame()}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameNameBody(
    uiState: addGameUiState,
    navigateBack: () -> Unit,
    updateGameName:(String)->Unit,
    addGame:()->Unit
){
    Scaffold (
        topBar = {
            Column(){
                ApptopBar(
                    destinationData = addGameWithNameDestination,
                    navigateUp = navigateBack
                )
                ElevatedCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.background),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,

                        ),
                    shape = RoundedCornerShape(30.dp),
                    elevation = CardDefaults.cardElevation(15.dp)
                ){
                    OutlinedTextField(
                        modifier = Modifier
//                            .weight(1f)
                            .fillMaxWidth()
                            .padding(4.dp),
                        value = uiState.gameName,
                        singleLine = true,
                        onValueChange = updateGameName,
                        textStyle = TextStyle.Default.copy(
                            fontSize = 20.sp,

                            ),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0, 0, 0, alpha = 0),
                            unfocusedBorderColor = Color(0, 0, 0, alpha = 0),
                            disabledBorderColor = Color(0, 0, 0, alpha = 0),
                        ),
                    )
                }
            }

        },
        floatingActionButton = {
            FloatingActionButton(onClick = addGame) {
                Icon(painter = painterResource(R.drawable.send), contentDescription = null)
            }
        }
    ){
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
           items(
               items = uiState.newMembers
           ){
               member->
               SingleGroupPerson(
                   chatUser = member,
                   chatAdded = false,
                   toggleUser = { _,_-> },
                   isCardEnabled = false,
                   modifier = Modifier
                       .clickable(enabled = false){},
               )
           }
        }
    }
}

@Composable
@Preview
fun AddGameWithNamePreview(){
    FiyamaTheme {
        AddGameNameBody(
            uiState = addGameUiState(
                newMembers = listOf(
                    user(username = "Testing 1"),
                    user(username = "Testing 1"),
                    user(username = "Testing 1"),
                    user(username = "Testing 1"),
                    user(username = "Testing 1"),
                )
            ),
            navigateBack = {},
            updateGameName = {},
            addGame = {}
        )
    }
}