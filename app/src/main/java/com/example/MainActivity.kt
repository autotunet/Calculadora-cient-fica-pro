package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ads.AdMobManager
import com.example.data.AppDatabase
import com.example.data.CalculationRepository
import com.example.ui.CalculatorScreen
import com.example.ui.CalculatorViewModel
import com.example.ui.CalculatorViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CalculationRepository(database.calculationDao())
        CalculatorViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AdMob
        AdMobManager.initialize(applicationContext)
        window.decorView.post {
            AdMobManager.showStartupAdIfAvailable(this)
        }

        setContent {
            MyApplicationTheme {
                CalculatorScreen(
                    viewModel = viewModel,
                    onToolUsed = { force -> AdMobManager.onToolUsed(this, force) }
                )
            }
        }
    }
}
