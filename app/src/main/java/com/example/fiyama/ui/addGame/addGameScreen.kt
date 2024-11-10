package com.example.fiyama.ui.addGame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.navArgument
import com.example.fiyama.data.user
import com.example.fiyama.ui.ApptopBar
import com.example.fiyama.ui.FancyLoading
import com.example.fiyama.ui.destination
import com.example.fiyama.ui.theme.FiyamaTheme
import com.example.fiyama.R

object addGameDestination : destination {
    override val route: String = "add_game"
    override val title: String = "Create new game"
    override val canGoBack: Boolean = true

}

@Composable
fun AddGameScreen(
    viewModel: addGameViewModel,
    navigateUp:()->Unit,
    navigateToAddGameWithName: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    AddGameBody(
        addGroupUiState = uiState,
        searchChange = {viewModel.searchChange(it)},
        searchUser = {viewModel.searchUser()},
        toggleUser = {checked,username->viewModel.toggleUser(checked,username)},
        navigateToAddGroupWithName = navigateToAddGameWithName,
        navigateUp = navigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameBody(
    addGroupUiState: addGameUiState,
    searchChange:(String)->Unit,
    searchUser:()->Unit,
    toggleUser:(Boolean,String)->Unit,
    navigateToAddGroupWithName:()->Unit,
    navigateUp:()->Unit,
) {
    var expandedSearchBar by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Column {
                ApptopBar(destinationData = addGameDestination,
                    navigateUp = navigateUp)

                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = addGroupUiState.searchQuery,
                            onQueryChange = { searchChange(it) },
                            onSearch = {
                                searchUser()
                                expandedSearchBar = false
                            },
                            expanded = expandedSearchBar,
                            onExpandedChange = { expandedSearchBar = it },
                            placeholder = { Text(text = "Search User...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            }
                        )
                    },
                    expanded = expandedSearchBar,
                    onExpandedChange = { expandedSearchBar = it },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "No sugesstion available, Will be implemented in future updates",
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        bottomBar = {
            if (addGroupUiState.newMembers.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp,
                        topStart = 15.dp,
                        topEnd = 15.dp
                    )
                ) {
                    Text(
                        text = "Members : ${addGroupUiState.newMembers.map { it.username.removeSuffix("@mafiya.com") }.joinToString(", ")}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            if (addGroupUiState.newMembers.isNotEmpty()) {
                FloatingActionButton(onClick = { navigateToAddGroupWithName() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                }
            }
        },
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        if (addGroupUiState.isLoading) {
            FancyLoading(isLoading = addGroupUiState.isLoading)
        } else if (addGroupUiState.isError) {
//            ShowErrorAndRetry {
//                searchChange("mrugen")
//            }
        } else if (addGroupUiState.searchQuery.length <= 3 && addGroupUiState.searchQuery.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(it)
                    .padding(10.dp)
                    .fillMaxWidth(),
                text = "Start typing letter more that 3 letters...",
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        } else if (addGroupUiState.searchQuery.length >= 3 && !addGroupUiState.searched) {
            Text(
                modifier = Modifier
                    .padding(it)
                    .padding(10.dp)
                    .fillMaxWidth(),
                text = "Press the button to start search.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        } else if (addGroupUiState.chatUsers.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(it)
                    .padding(10.dp)
                    .fillMaxWidth(),
                text = "No User found try with different query"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
//                items(
//                    items = addGroupUiState.chatUsers) {
//                    sing->
//                    SingleGroupPerson(
//                        chatUser = sing,
//                        toggleUser = toggleUser,
//                        isCardEnabled = true,
//                        chatAdded = addGroupUiState.newMembers.contains(it.username),
//                    )
//                }
                items(
                    items = addGroupUiState.chatUsers
                ) {
                    SingleGroupPerson(
                        chatUser = it,
                        toggleUser = toggleUser,
                        isCardEnabled = true,
                        chatAdded = addGroupUiState.newMembers.contains(it))
                }
            }
        }

    }
}

@Composable
fun SingleGroupPerson(
    chatUser: user,
    chatAdded: Boolean,
    toggleUser: (Boolean, String) -> Unit,
    isCardEnabled: Boolean,
    modifier: Modifier = Modifier
) {
//    val (checkedState, onStateChange) = remember { mutableStateOf(true) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp)
            .toggleable(
                value = chatAdded,
                onValueChange = { toggleUser(it, chatUser.username) }
            ),

//        enabled = isCardEnabled
    ) {
        Row(
            modifier = modifier
                .fillMaxHeight()
//                .width(intrinsicSize = IntrinsicSize.Max)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = modifier
                    .size(40.dp),
                painter = (if (chatAdded) {
                    painterResource(R.drawable.check)
                } else {
                    painterResource(R.drawable.person)
                }),
                contentDescription = ""
            )
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = chatUser.username.removeSuffix("@mafiya.com") ,
                fontSize = 25.sp,
                fontWeight = FontWeight(700),
                textAlign = TextAlign.Center
            )
//            Checkbox(
//                modifier = Modifier
//                    .padding(end = 10.dp)
//                    ,
//                checked = chatAdded,
//                onCheckedChange = null,
//            )
        }

    }
}

@Composable
@Preview
fun PreiewAddGameScreen() {
    FiyamaTheme {
        AddGameBody(
            addGroupUiState = addGameUiState(),
            toggleUser = { _, _ -> },
            searchChange = {},
            searchUser = {},
            navigateToAddGroupWithName = {},
            navigateUp = {}
        )
    }
}
