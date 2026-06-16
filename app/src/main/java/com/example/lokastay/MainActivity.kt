package com.example.lokastay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.lokastay.data.DatabaseSeeder
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.ui.LokastayApp
import com.example.lokastay.ui.theme.LokastayTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(this)
        lifecycleScope.launch {
            DatabaseSeeder.seedIfNeeded(db)
        }

        setContent {
            LokastayTheme {
                LokastayApp()
            }
        }
    }
}