package com.example.lokastay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lokastay.data.database.DatabaseProvider
import com.example.lokastay.data.entity.Favorite
import com.example.lokastay.data.entity.FavoriteCollection
import com.example.lokastay.data.entity.Villa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val favorites: List<Favorite> = emptyList(),
    val favoriteVillas: List<Villa> = emptyList(),
    val collections: List<FavoriteCollection> = emptyList(),
    val message: String = ""
)

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState

    fun loadFavorites(userId: Int) {
        scope.launch {
            val favoriteList = db.favoriteDao().getFavorites(userId)

            val uniqueVillaIds = favoriteList.map { it.villaId }.distinct()
            val villaList = uniqueVillaIds.mapNotNull { vId ->
                try {
                    db.villaDao().getVillaById(vId)
                } catch (e: Exception) {
                    null
                }
            }

            _uiState.value = _uiState.value.copy(
                favorites = favoriteList,
                favoriteVillas = villaList,
                message = "Favorites loaded successfully"
            )
        }
    }

    fun loadCollections(userId: Int) {
        scope.launch {
            val collectionList = db.collectionDao().getCollectionsByUserId(userId)
            _uiState.value = _uiState.value.copy(
                collections = collectionList
            )
        }
    }

    fun createCollection(userId: Int, name: String) {
        scope.launch {
            db.collectionDao().insertCollection(FavoriteCollection(userId = userId, name = name))
            loadCollections(userId)
        }
    }

    fun deleteCollection(collectionId: Int, userId: Int) {
        scope.launch {
            db.collectionDao().deleteCollectionById(collectionId)
            db.favoriteDao().deleteFavoritesByCollection(collectionId)
            loadCollections(userId)
            loadFavorites(userId)
        }
    }

    fun renameCollection(collectionId: Int, newName: String, userId: Int) {
        scope.launch {
            db.collectionDao().updateCollectionName(collectionId, newName)
            loadCollections(userId)
        }
    }

    fun addFavorite(userId: Int, villaId: Int, collectionId: Int) {
        scope.launch {
            db.favoriteDao().addFavorite(
                Favorite(
                    userId = userId,
                    villaId = villaId,
                    collectionId = collectionId
                )
            )
            loadFavorites(userId)
        }
    }

    fun removeFavorite(userId: Int, villaId: Int) {
        scope.launch {
            db.favoriteDao().removeAllFavoritesByVilla(userId, villaId)
            loadFavorites(userId)
        }
    }

    fun toggleFavoriteInCollection(userId: Int, villaId: Int, collectionId: Int, isAlreadySaved: Boolean) {
        scope.launch {
            if (isAlreadySaved) {
                db.favoriteDao().removeFavoriteFromCollection(userId, villaId, collectionId)
            } else {
                db.favoriteDao().addFavorite(
                    Favorite(userId = userId, villaId = villaId, collectionId = collectionId)
                )
            }
            loadFavorites(userId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}