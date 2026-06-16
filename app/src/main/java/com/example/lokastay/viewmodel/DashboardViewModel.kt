package com.example.lokastay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.Transaction
import com.example.lokastay.data.entity.Villa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val favoriteVillas: List<Villa> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val successTransactions: List<Transaction> = emptyList(),
    val failedTransactions: List<Transaction> = emptyList(),
    val message: String = ""
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    fun loadDashboard(userId: Int) {
        scope.launch {
            val favoriteList = db.favoriteDao().getFavorites(userId)
            val villaList = favoriteList.mapNotNull { favorite ->
                try {
                    db.villaDao().getVillaById(favorite.villaId)
                } catch (e: Exception) {
                    null
                }
            }

            val transactionList = db.transactionDao().getUserTransactions(userId)
            val successList = transactionList.filter { it.paymentStatus == "SUCCESS" }
            val failedList = transactionList.filter { it.paymentStatus == "FAILED" }

            _uiState.value = DashboardUiState(
                favoriteVillas = villaList,
                allTransactions = transactionList,
                successTransactions = successList,
                failedTransactions = failedList,
                message = "Dashboard loaded successfully"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}