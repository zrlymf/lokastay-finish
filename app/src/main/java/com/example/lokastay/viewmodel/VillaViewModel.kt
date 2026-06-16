package com.example.lokastay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.Villa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class VillaUiState(
    val villas: List<Villa> = emptyList(),
    val message: String = ""
)

class VillaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _uiState = MutableStateFlow(VillaUiState())
    val uiState: StateFlow<VillaUiState> = _uiState

    init {
        loadAllVillas()
    }

    fun loadAllVillas() {
        scope.launch {
            val villaList = db.villaDao().getAllVillas()
            _uiState.value = VillaUiState(
                villas = villaList,
                message = "Villa data loaded successfully"
            )
        }
    }

    fun filterVilla(
        location: String,
        guestCount: Int = 1,
        checkInDate: String = "",
        checkOutDate: String = "",
        rating: Float = 0f,
        minPrice: Double = 0.0,
        maxPrice: Double = 10000000.0,
        requireWifi: Boolean = false,
        requirePool: Boolean = false,
        requireAc: Boolean = false,
        requireLaundry: Boolean = false,
        requireTv: Boolean = false,
        requireGym: Boolean = false,
        requireHeater: Boolean = false,
        requireParking: Boolean = false,
        requireBbq: Boolean = false,
        requireBathtub: Boolean = false,
        requireGarden: Boolean = false,
        requireKitchen: Boolean = false
    ) {
        scope.launch {
            val result = db.villaDao().filterVilla(
                location = location,
                guestCount = guestCount,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                rating = rating,
                minPrice = minPrice,
                maxPrice = maxPrice,
                requireWifi = requireWifi, requirePool = requirePool, requireAc = requireAc,
                requireLaundry = requireLaundry, requireTv = requireTv, requireGym = requireGym,
                requireHeater = requireHeater, requireParking = requireParking, requireBbq = requireBbq,
                requireBathtub = requireBathtub, requireGarden = requireGarden, requireKitchen = requireKitchen
            )
            _uiState.value = VillaUiState(
                villas = result,
                message = "Number of villas found: ${result.size}"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}