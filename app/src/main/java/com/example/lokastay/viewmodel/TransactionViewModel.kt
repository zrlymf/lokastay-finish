package com.example.lokastay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val bookedTransactions: List<Transaction> = emptyList(),
    val totalPrice: Double = 0.0,
    val message: String = "",
    val draftCheckIn: String = "",
    val draftCheckOut: String = "",
    val draftGuests: Int = 0,
    val draftRequests: Set<String> = emptySet(),
    val draftName: String = "",
    val draftEmail: String = "",
    val draftPhone: String = "",
    val draftPaymentMethod: String = "",
    val draftPromoTitle: String = "",
    val availablePoints: Int = 0
)

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState

    fun loadUserPoints(userId: Int) {
        scope.launch {
            try {
                val user = db.userDao().getUserById(userId)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        availablePoints = user.currentPoints
                    )
                }
            } catch (e: Exception) {
            }
        }
    }

    fun saveDraftBooking(checkIn: String, checkOut: String, guests: Int, requests: Set<String>) {
        _uiState.value = _uiState.value.copy(
            draftCheckIn = checkIn,
            draftCheckOut = checkOut,
            draftGuests = guests,
            draftRequests = requests
        )
    }

    fun saveDraftGuest(name: String, email: String, phone: String) {
        _uiState.value = _uiState.value.copy(
            draftName = name,
            draftEmail = email,
            draftPhone = phone
        )
    }

    fun saveDraftPayment(method: String, promoTitle: String) {
        _uiState.value = _uiState.value.copy(
            draftPaymentMethod = method,
            draftPromoTitle = promoTitle
        )
    }

    fun calculateTotalPrice(villaId: Int, nights: Int) {
        scope.launch {
            try {
                val villa = db.villaDao().getVillaById(villaId)
                val total = villa.pricePerNight * nights
                _uiState.value = _uiState.value.copy(
                    totalPrice = total,
                    message = "Total price calculated successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    totalPrice = 0.0,
                    message = "Villa not found"
                )
            }
        }
    }

    fun createTransaction(userId: Int, villaId: Int, checkInDate: String, checkOutDate: String, nights: Int, isSuccess: Boolean, usePoints: Boolean) {
        scope.launch {
            try {
                if (checkInDate.isNotBlank() && checkOutDate.isNotBlank()) {
                    val overlaps = db.transactionDao().getOverlappingTransactions(villaId, checkInDate, checkOutDate)
                    if (overlaps.isNotEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            message = "Sorry, these dates are already booked. Please choose another date."
                        )
                        return@launch
                    }
                }

                val villa = db.villaDao().getVillaById(villaId)
                val basePrice = villa.pricePerNight * nights
                val tax = basePrice * 0.10
                var finalTotal = basePrice + tax

                val user = db.userDao().getUserById(userId)
                var pointsUsed = 0
                var pointsEarned = 0

                if (user != null) {
                    if (usePoints && user.currentPoints > 0) {
                        val maxDiscountValue = user.currentPoints * 100.0
                        if (maxDiscountValue >= finalTotal) {
                            pointsUsed = (finalTotal / 100).toInt()
                            finalTotal = 0.0
                        } else {
                            pointsUsed = user.currentPoints
                            finalTotal -= maxDiscountValue
                        }
                    }

                    if (isSuccess) {
                        pointsEarned = (finalTotal / 10000).toInt()
                        val newCurrentPoints = user.currentPoints - pointsUsed + pointsEarned
                        val newAccumulatedPoints = user.accumulatedPoints + pointsEarned

                        val newTier = when {
                            newAccumulatedPoints >= 5000 -> "Platinum"
                            newAccumulatedPoints >= 1000 -> "Gold"
                            newAccumulatedPoints >= 500 -> "Silver"
                            else -> "Classic"
                        }

                        db.userDao().update(
                            user.copy(
                                currentPoints = newCurrentPoints,
                                accumulatedPoints = newAccumulatedPoints,
                                memberTier = newTier
                            )
                        )
                    }
                }

                val paymentStatus = if (isSuccess) "SUCCESS" else "FAILED"

                val transaction = Transaction(
                    userId = userId,
                    villaId = villaId,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    nights = nights,
                    totalPrice = finalTotal,
                    paymentStatus = paymentStatus,
                    pointsUsed = pointsUsed,
                    pointsEarned = pointsEarned
                )

                db.transactionDao().insert(transaction)
                val updatedTransactions = db.transactionDao().getUserTransactions(userId)

                _uiState.value = TransactionUiState(
                    transactions = updatedTransactions,
                    totalPrice = finalTotal,
                    message = "Transaction created with status: $paymentStatus",
                    draftCheckIn = "", draftCheckOut = "", draftGuests = 0, draftRequests = emptySet(),
                    draftName = "", draftEmail = "", draftPhone = "",
                    draftPaymentMethod = "", draftPromoTitle = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to create transaction"
                )
            }
        }
    }

    fun loadUserTransactions(userId: Int) {
        scope.launch {
            val transactionList = db.transactionDao().getUserTransactions(userId)
            _uiState.value = _uiState.value.copy(
                transactions = transactionList,
                message = "Transaction history loaded successfully"
            )
        }
    }

    fun loadBookedDates(villaId: Int) {
        scope.launch {
            try {
                val transactions = db.transactionDao().getBookedTransactionsByVilla(villaId)
                _uiState.value = _uiState.value.copy(
                    bookedTransactions = transactions
                )
            } catch (e: Exception) {
            }
        }
    }

    fun cancelTransaction(transactionId: Int, userId: Int) {
        scope.launch {
            try {
                db.transactionDao().cancelTransaction(transactionId)
                loadUserTransactions(userId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "Failed to cancel transaction")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}