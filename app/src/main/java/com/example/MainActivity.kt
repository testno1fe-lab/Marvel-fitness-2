package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.MarvelApp
import com.example.ui.theme.MarvelBlack
import com.example.ui.theme.MarvelFitnessTheme
import com.example.ui.viewmodel.MarvelViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MarvelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarvelFitnessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MarvelBlack
                ) {
                    MarvelApp(viewModel = viewModel)
                }
            }
        }
    }
}
