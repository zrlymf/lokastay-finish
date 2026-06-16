package com.example.lokastay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.Review
import com.example.lokastay.data.entity.Villa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val villa: Villa? = null,
    val reviews: List<Review> = emptyList(),
    val message: String = ""
)

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadDetail(villaId: Int) {
        scope.launch {
            try {
                val villa = db.villaDao().getVillaById(villaId)
                val reviewList = db.reviewDao().getReviews(villaId)

                val sortedReviews = reviewList.sortedByDescending { it.id }

                _uiState.value = DetailUiState(
                    villa = villa,
                    reviews = sortedReviews,
                    message = "Villa details loaded successfully"
                )
            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    villa = null,
                    reviews = emptyList(),
                    message = "Villa not found"
                )
            }
        }
    }

    fun addReview(villaId: Int, username: String, rating: Float, comment: String) {
        scope.launch {
            try {
                val newReview = Review(
                    villaId = villaId,
                    username = username,
                    rating = rating,
                    comment = comment
                )

                db.reviewDao().insert(newReview)

                loadDetail(villaId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to add review"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}