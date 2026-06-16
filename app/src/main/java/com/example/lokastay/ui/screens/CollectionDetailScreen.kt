package com.example.lokastay.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lokastay.data.entity.Villa
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.FavoriteViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CollectionDetailScreen(
    collectionId: Int,
    onBackClick: () -> Unit,
    onVillaClick: (Int) -> Unit,
    favoriteViewModel: FavoriteViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val userId = authState.loggedInUserId
    val favoriteState by favoriteViewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != -1) {
            favoriteViewModel.loadCollections(userId)
            favoriteViewModel.loadFavorites(userId)
        }
    }

    val collection = favoriteState.collections.find { it.id == collectionId }
    val collectionName = collection?.name ?: "Collection"

    val favsInCol = favoriteState.favorites.filter { it.collectionId == collectionId }
    val villaIds = favsInCol.map { it.villaId }
    val villasToShow = favoriteState.favoriteVillas.filter { it.id in villaIds }

    val filteredVillas = villasToShow.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth().zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 4.dp, bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }

                Text(
                    text = "List Properties",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Menu", tint = Color.Black)
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            onClick = {
                                expandedMenu = false
                                showRenameDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                expandedMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = collectionName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "${villasToShow.size} Properties", fontSize = 14.sp, color = MainCyan)
        }

        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            placeholder = { Text("Search properties", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = { Icon(Icons.Outlined.Tune, contentDescription = "Filter", tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedBorderColor = MainCyan,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (filteredVillas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No properties found in this collection.", color = Color.Gray, fontSize = 15.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredVillas) { villa ->
                    CollectionVillaCard(
                        villa = villa,
                        onVillaClick = { onVillaClick(villa.id) },
                        onRemoveFavorite = {
                            favoriteViewModel.toggleFavoriteInCollection(
                                userId = userId,
                                villaId = villa.id,
                                collectionId = collectionId,
                                isAlreadySaved = true
                            )
                        }
                    )
                }
            }
        }
    }

    if (showRenameDialog) {
        RenameCollectionDialog(
            initialName = collectionName,
            onDismiss = { showRenameDialog = false },
            onSave = { newName ->
                favoriteViewModel.renameCollection(collectionId, newName, userId)
                showRenameDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteCollectionDialog(
            collectionName = collectionName,
            propertyCount = villasToShow.size,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                favoriteViewModel.deleteCollection(collectionId, userId)
                showDeleteDialog = false
                onBackClick()
            }
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun CollectionVillaCard(
    villa: Villa,
    onVillaClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(villa.imageUrl) {
        context.resources.getIdentifier(villa.imageUrl, "drawable", context.packageName)
    }

    val formattedPrice = remember(villa.pricePerNight) {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(villa.pricePerNight.toLong())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onVillaClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = imageResId,
                contentDescription = villa.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { onRemoveFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove from Collection",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = villa.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = villa.rating.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = villa.location,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = "Rp $formattedPrice", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MainCyan)
            Text(text = " /night", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
        }
    }
}