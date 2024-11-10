package com.example.fiyama.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.fiyama.ui.addGame.AddGameScreen
import com.example.fiyama.ui.addGame.AddGameWithName
import com.example.fiyama.ui.addGame.addGameDestination
import com.example.fiyama.ui.addGame.addGameViewModel
import com.example.fiyama.ui.addGame.addGameWithNameDestination
import com.example.fiyama.ui.currentGame.GameRoomScreen
import com.example.fiyama.ui.currentGame.gameRoomDestination
import com.example.fiyama.ui.games.GameStartScreen
import com.example.fiyama.ui.games.gameScreen
import com.example.fiyama.ui.startHere.LoginScreen
import com.example.fiyama.ui.startHere.WelcomeStart

import com.example.fiyama.ui.startHere.loginDestination
import com.example.fiyama.ui.startHere.welcomeDestination
import com.example.fiyama.ui.theme.FiyamaTheme


interface destination {
    val route: String
    val title: String
    val canGoBack: Boolean
}

@Composable
fun ApplicationScreen(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(welcomeDestination.route) {
            WelcomeStart(
                navigateToGameRoom = {
                    navController.navigate(gameScreen.route) {
                        popUpTo(0)
                    }
                },
                navigateToLogin = {
                    navController.navigate(loginDestination.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(loginDestination.route) {
            LoginScreen(
                navigateToGameRoom = {
                    navController.navigate(gameScreen.route) {
                        popUpTo(0)
                    }
                },
                navigateToSignup = {
                    navController.navigate(welcomeDestination.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(gameScreen.route) {
            GameStartScreen(
                navigateToAddChat = {
                    navController.navigate("${addGameDestination.route}/$it")
                },
                navigateToGame = {
                    navController.navigate("${gameRoomDestination.route}/$it")
                },
                navigateToWelcome = {navController.navigate(welcomeDestination.route){
                    popUpTo(0)
                } }
            )
        }

        composable(route = "${gameRoomDestination.route}/{roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )) {
            GameRoomScreen(
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }

        navigation(
            startDestination = "${addGameDestination.route}/{currentUser}",
            route = "addGame"
        ) {
            composable(route = "${addGameDestination.route}/{currentUser}",
                arguments = listOf(
                    navArgument("currentUser") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val parentEntry = remember {
                    navController.getBackStackEntry("addGame")
                }
                val addGameViewModel = viewModel<addGameViewModel>(
                    parentEntry,
                    factory = GodViewmodelProvider.Factory
                )
                AddGameScreen(
                    viewModel = addGameViewModel,
                    navigateUp = {
                        navController.navigateUp()
                    },
                    navigateToAddGameWithName = {
                        navController.navigate(addGameWithNameDestination.route)
                    }
                )
            }

            composable(
                route = addGameWithNameDestination.route,
            ) { backStackEntry ->
                val parentEntry = remember {
                    navController.getBackStackEntry("addGame")
                }
                val addGameViewModel = viewModel<addGameViewModel>(
                    parentEntry,
                    factory = GodViewmodelProvider.Factory
                )
                AddGameWithName(
                    viewModel = addGameViewModel,
                    navigateBack = { navController.navigateUp() },
                    navigateSuccess = {
                        navController.navigate(gameScreen.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApptopBar(
    destinationData: destination,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    navigateUp: () -> Unit,
    action: @Composable (RowScope.() -> Unit) = {},
    modifier: Modifier = Modifier,
    canGoBack: Boolean = false,
    goBack: () -> Unit = {}
) {
    TopAppBar(
        title = {
            if (title == "") {
                Text(
                    text = destinationData.title,
//                color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = title)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary
//            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon =
        {
            if (destinationData.canGoBack) {

                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
//                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

            } else if (canGoBack) {
                BackHandler(enabled = canGoBack) {
                    goBack()
                }
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

        },
        actions = action,
        scrollBehavior = scrollBehavior,

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ApptopBarPreview() {
    FiyamaTheme(dynamicColor = false) {
        ApptopBar(
            gameScreen,
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            navigateUp = {},
//            title = "Testing this",
//            canGoBack = true
        )
    }
}
