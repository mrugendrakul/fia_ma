package com.example.fiyama.ui.startHere

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiyama.R
import com.example.fiyama.ui.ApptopBar
import com.example.fiyama.ui.FancyLoading
import com.example.fiyama.ui.GodViewmodelProvider
import com.example.fiyama.ui.destination
import kotlin.math.log

object loginDestination : destination {
    override val route: String = "login"
    override val title: String = "Login"
    override val canGoBack: Boolean = false
}

@Composable
fun LoginScreen(
    welViewmodel: welViewmodel = viewModel(factory = GodViewmodelProvider.Factory),
    navigateToGameRoom: () -> Unit,
    navigateToSignup: () -> Unit
) {
    val uiState = welViewmodel.welState.collectAsState()
    if (uiState.value.successSignal) {
        welViewmodel.resetSuccessSignal()
        navigateToGameRoom()
    }
    Loginbody(
        uiState = uiState.value,
        usernameChange = {
            welViewmodel.updateUsername(it)
        },
        passwordChange = {
            welViewmodel.updatePassword(it)
        },
        login = {
            welViewmodel.loginButton()
        },
        navigateToSignup = {
            navigateToSignup()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Loginbody(
    uiState: welState,
    usernameChange: (String) -> Unit,
    passwordChange: (String) -> Unit,
    login: () -> Unit,
    navigateToSignup: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    Scaffold(
//        modifier = Modifier.verticalScroll(rememberScrollState()),
        topBar = {
            ApptopBar(
                destinationData = loginDestination,
                navigateUp = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            if (uiState.isError) {
                Text(
                    text = "Error : ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Welcome to Fiyama!",
//                    color = MaterialTheme.colorScheme.error,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            OutlinedTextField(
                value = uiState.username,
                onValueChange = usernameChange,
                label = {
                    Text(text = stringResource(R.string.username))
                },
//                isError = startUiState.emptyUsername || startUiState.usernameExist,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.person),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = ""
                    )
                }
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = passwordChange,
                label = {
                    Text(text = stringResource(R.string.password))
                },
//                isError = startUiState.emptyPassword,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (!passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    val icon = if (passwordVisible)
                        painterResource(R.drawable.visibility_off)
                    else
                        painterResource(R.drawable.visibility)
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = icon,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = ""
                        )
                    }
                }
            )

            Button(
                onClick = {
                    login()
                },
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {
                Text(stringResource(R.string.login_now))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.no_account_signup_now))
                TextButton(
                    onClick = {
                        navigateToSignup()
                    },
                    modifier = Modifier
                ) {
                    Text(
                        stringResource(R.string.sign_up)
                    )
                }
            }

        }

    }
    FancyLoading(isLoading = uiState.isLoading)
}

@Composable
@Preview
fun PreviewLogin() {
    Loginbody(
        uiState = welState(),
        usernameChange = {},
        passwordChange = {},
        login = {},
        navigateToSignup = {}
    )

}