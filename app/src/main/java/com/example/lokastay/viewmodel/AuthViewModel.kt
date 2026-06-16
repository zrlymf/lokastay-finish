package com.example.lokastay.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.SessionManager
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val loggedInUserId: Int = -1,
    val userName: String = "",
    val message: String = ""
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val sessionManager = SessionManager(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        val isLoggedIn = sessionManager.isLoggedIn()
        val userId = sessionManager.getLoggedInUserId()
        val userName = sessionManager.getLoggedInUserName()

        _uiState.value = AuthUiState(
            isLoggedIn = isLoggedIn,
            loggedInUserId = userId,
            userName = userName,
            message = if (isLoggedIn) "User already logged in" else "Not logged in"
        )
    }

    fun login(email: String, password: String) {
        scope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.value = AuthUiState(
                    isLoggedIn = false, loggedInUserId = -1, message = "Email and password are required"
                )
                return@launch
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.value = AuthUiState(
                    isLoggedIn = false, loggedInUserId = -1, message = "Invalid email format"
                )
                return@launch
            }
            val user = db.userDao().login(email, password)
            if (user != null) {
                sessionManager.saveLogin(user.id, user.name)

                _uiState.value = AuthUiState(
                    isLoggedIn = true,
                    loggedInUserId = user.id,
                    userName = user.name,
                    message = "Login successful"
                )
            } else {
                _uiState.value = AuthUiState(
                    isLoggedIn = false, loggedInUserId = -1, message = "Incorrect email or password"
                )
            }
        }
    }

    fun register(name: String, email: String, phone: String, password: String) {
        scope.launch {
            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                _uiState.value = AuthUiState(
                    isLoggedIn = false,
                    loggedInUserId = -1,
                    message = "All fields are required"
                )
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.value = AuthUiState(
                    isLoggedIn = false,
                    loggedInUserId = -1,
                    message = "Invalid email format"
                )
                return@launch
            }

            if (password.length < 6) {
                _uiState.value = AuthUiState(
                    isLoggedIn = false,
                    loggedInUserId = -1,
                    message = "Password must be at least 6 characters"
                )
                return@launch
            }

            val existingUser = db.userDao().getUserByEmail(email)

            if (existingUser != null) {
                _uiState.value = AuthUiState(
                    isLoggedIn = false,
                    loggedInUserId = -1,
                    message = "Email is already registered"
                )
                return@launch
            }

            val currentYear = LocalDate.now().year
            val userCount = db.userDao().countUsers()
            val nextNumber = userCount + 1
            val formattedNumber = String.format(Locale.US, "%03d", nextNumber)
            val newMemberNumber = "LS-$currentYear-$formattedNumber"

            db.userDao().insert(
                User(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    memberNumber = newMemberNumber,
                    currentPoints = 0,
                    accumulatedPoints = 0,
                    memberTier = "Classic"
                )
            )

            _uiState.value = AuthUiState(
                isLoggedIn = false,
                loggedInUserId = -1,
                message = "Registration successful"
            )
        }
    }

    fun logout() {
        sessionManager.clearLogin()
        _uiState.value = AuthUiState(
            isLoggedIn = false,
            loggedInUserId = -1,
            message = "Logout successful"
        )
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}