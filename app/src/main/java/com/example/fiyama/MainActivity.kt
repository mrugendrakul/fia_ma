package com.example.fiyama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.fiyama.ui.ApplicationScreen
import com.example.fiyama.ui.theme.FiyamaTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiyamaTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) {
////                    ApplicationScreen(
////                        navController = rememberNavController(),
////                        modifier = Modifier.padding(innerPadding),
////                        startDestination = "welcome"
////                    )
//                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currUser = Firebase.auth.currentUser
        if (currUser == null) {
            setContent {
                FiyamaTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                            .imePadding(),
//                        color = MaterialTheme.colorScheme.background
                    ) {
                        ApplicationScreen(
                            navController = rememberNavController(),
                            startDestination = "welcome",
//                            modifier = Modifier.padding(it)
                        )
                    }
                }
            }

        }
        else{
            setContent {
                FiyamaTheme {
                    Surface (
                        modifier = Modifier.fillMaxSize()
                            .imePadding(),
//                        color = MaterialTheme.colorScheme.background
                    ) {
                        ApplicationScreen(
                            navController = rememberNavController(),
                            startDestination = "game",
//                            modifier = Modifier.padding(it)
                        )
                    }
                }
            }
        }
    }
}