package com.example.lokastay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val message: String = ""
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadUserProfile(userId: Int) {
        scope.launch {
            try {
                val user = db.userDao().getUserById(userId)
                _uiState.value = ProfileUiState(user = user)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(message = "Failed to load profile")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}