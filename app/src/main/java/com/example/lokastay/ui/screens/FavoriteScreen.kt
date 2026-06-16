package com.example.lokastay.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lokastay.R
import com.example.lokastay.data.entity.FavoriteCollection
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(
    onCollectionClick: (Int) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToBooking: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    favoriteViewModel: FavoriteViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val userId = authState.loggedInUserId
    val favoriteState by favoriteViewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        if (userId != -1) {
            favoriteViewModel.loadCollections(userId)
            favoriteViewModel.loadFavorites(userId)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<FavoriteCollection?>(null) }
    var collectionToRename by remember { mutableStateOf<FavoriteCollection?>(null) }

    val collectionsWithData = favoriteState.collections.map { col ->
        val favsInCol = favoriteState.favorites.filter { it.collectionId == col.id }
        val count = favsInCol.size

        val firstVillaId = favsInCol.firstOrNull()?.villaId
        val firstVilla = favoriteState.favoriteVillas.find { it.id == firstVillaId }
        val coverImage = firstVilla?.imageUrl ?: "villa_1"

        Triple(col, count, coverImage)
    }

    val filteredCollections = collectionsWithData.filter {
        it.first.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            LokastayBottomNavigationBar(
                currentTab = "favorite",
                onNavigateToHome = onNavigateToHome,
                onNavigateToFavorite = { },
                onNavigateToBooking = onNavigateToBooking,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 4.dp, bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(48.dp)
                    )

                    Text(
                        text = "My Favorites",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search collection", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
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

            if (favoriteState.collections.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_empty_favorite),
                        contentDescription = "Empty Favorites",
                        modifier = Modifier.size(280.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "No Favorites Yet",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Start adding items to your favorites list\nfor quick access",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                }
            } else if (filteredCollections.isEmpty() && searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No collection matches your search.", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCollections) { data ->
                        CollectionCard(
                            collectionName = data.first.name,
                            propertyCount = data.second,
                            imageUrl = data.third,
                            onClick = { onCollectionClick(data.first.id) },
                            onRenameClick = { collectionToRename = data.first },
                            onDeleteClick = { showDeleteDialog = data.first }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { collection ->
        val count = favoriteState.favorites.count { it.collectionId == collection.id }
        DeleteCollectionDialog(
            collectionName = collection.name,
            propertyCount = count,
            onDismiss = { showDeleteDialog = null },
            onDelete = {
                favoriteViewModel.deleteCollection(collection.id, userId)
                showDeleteDialog = null
            }
        )
    }

    collectionToRename?.let { collection ->
        RenameCollectionDialog(
            initialName = collection.name,
            onDismiss = { collectionToRename = null },
            onSave = { newName ->
                favoriteViewModel.renameCollection(collection.id, newName, userId)
                collectionToRename = null
            }
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun CollectionCard(
    collectionName: String,
    propertyCount: Int,
    imageUrl: String,
    onClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageResId = remember(imageUrl) { context.resources.getIdentifier(imageUrl, "drawable", context.packageName) }

    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = imageResId,
                contentDescription = "Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = collectionName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$propertyCount Properties",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Box {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { expandedMenu = true }
                )
                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = {
                            expandedMenu = false
                            onRenameClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            expandedMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RenameCollectionDialog(initialName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var newName by remember { mutableStateOf(initialName) }
    val isEnabled = newName.isNotBlank() && newName != initialName

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        text = "Rename Collection",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainCyan,
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onSave(newName) },
                    enabled = isEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainCyan,
                        disabledContainerColor = Color(0xFFEEEEEE),
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DeleteCollectionDialog(collectionName: String, propertyCount: Int, onDismiss: () -> Unit, onDelete: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MainCyan.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Delete $collectionName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Are you sure want to delete $propertyCount properties from your favorites?",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Text("Cancel", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}