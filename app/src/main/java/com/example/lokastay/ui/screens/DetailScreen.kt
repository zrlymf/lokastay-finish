package com.example.lokastay.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.DetailViewModel
import com.example.lokastay.viewmodel.FavoriteViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
fun DetailScreen(
    villaId: Int,
    checkInDate: String = "",
    checkOutDate: String = "",
    guestCount: Int = 0,
    onBackClick: () -> Unit,
    onNavigateToAllFacilities: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToBooking: (Int, String, String, Int) -> Unit = { _, _, _, _ -> },
    detailViewModel: DetailViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val detailState by detailViewModel.uiState.collectAsState()
    val favoriteState by favoriteViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val userId = authState.loggedInUserId

    var showCollectionSheet by remember { mutableStateOf(false) }
    var showCreateCollectionSheet by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }
    var selectedImageForFullscreen by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(villaId, userId) {
        detailViewModel.loadDetail(villaId)
        if (userId != -1) {
            favoriteViewModel.loadFavorites(userId)
            favoriteViewModel.loadCollections(userId)
        }
    }

    val villa = detailState.villa
    val isFavorite = favoriteState.favorites.any { it.villaId == villaId }
    val savedInCollections = favoriteState.favorites.filter { it.villaId == villaId }.map { it.collectionId }

    if (villa == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MainCyan)
        }
        return
    }

    val reviewCount = remember(villa.id) {
        when (villa.id) {
            1 -> 915; 2 -> 879; 3 -> 750; 4 -> 946; 5 -> 891
            6 -> 879; 7 -> 936; 8 -> 887; 9 -> 792; 10 -> 786
            11 -> 811; 12 -> 927; 13 -> 758; 14 -> 836; 15 -> 779
            else -> 850
        }
    }

    val context = LocalContext.current
    val getResId: (String) -> Int = { imageName ->
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

    val mainImageRes = remember(villa.imageUrl) { getResId(villa.imageUrl) }

    val formattedPrice = remember(villa.pricePerNight) {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(villa.pricePerNight.toLong())
    }

    val facilities = remember(villa) {
        val list = mutableListOf<Pair<String, ImageVector>>()
        if (villa.hasWifi) list.add("Wifi" to Icons.Default.Wifi)
        if (villa.hasPool) list.add("Pool" to Icons.Default.Pool)
        if (villa.hasAc) list.add("AC" to Icons.Default.AcUnit)
        if (villa.hasGym) list.add("Gym" to Icons.Default.FitnessCenter)
        if (villa.hasTv) list.add("TV" to Icons.Default.Tv)
        if (villa.hasLaundry) list.add("Laundry" to Icons.Default.LocalLaundryService)
        if (villa.hasHeater) list.add("Heater" to Icons.Default.HotTub)
        if (villa.hasParking) list.add("Parking" to Icons.Default.DirectionsCar)
        if (villa.hasBbq) list.add("BBQ" to Icons.Default.OutdoorGrill)
        if (villa.hasBathtub) list.add("Bathtub" to Icons.Default.Bathtub)
        if (villa.hasGarden) list.add("Garden" to Icons.Default.Park)
        if (villa.hasKitchen) list.add("Kitchen" to Icons.Default.Kitchen)

        if (list.isEmpty()) {
            list.add("Wifi" to Icons.Default.Wifi)
            list.add("Beach" to Icons.Default.BeachAccess)
        }
        list
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 32.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 20.dp, bottom = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Price", fontSize = 12.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Rp $formattedPrice", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(text = " /night", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                    Button(
                        onClick = { onNavigateToBooking(villa.id, checkInDate, checkOutDate, guestCount) },
                        colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.width(150.dp).height(54.dp)
                    ) {
                        Text(text = "Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp)
            ) {

                AsyncImage(
                    model = mainImageRes,
                    contentDescription = "Main Villa Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .clickable { selectedImageForFullscreen = villa.imageUrl }
                )

                if (villa.imageGallery.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        villa.imageGallery.take(4).forEach { imgString ->
                            AsyncImage(
                                model = getResId(imgString),
                                contentDescription = "Gallery Thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(3f / 4f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedImageForFullscreen = imgString }
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = villa.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                            modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            tint = if (isFavorite) Color(0xFFE53935) else Color.LightGray,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(32.dp).clickable {
                                if (userId != -1) {
                                    showCollectionSheet = true
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = villa.location, fontSize = 15.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToReviews() }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        val fullStars = villa.rating.toInt()
                        val hasHalfStar = (villa.rating - fullStars) >= 0.5f
                        val emptyStars = 5 - fullStars - (if (hasHalfStar) 1 else 0)

                        Row {
                            repeat(fullStars) { Icon(Icons.Default.Star, contentDescription = "Full Star", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp)) }
                            if (hasHalfStar) { Icon(Icons.AutoMirrored.Filled.StarHalf, contentDescription = "Half Star", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp)) }
                            repeat(emptyStars) { Icon(Icons.Outlined.StarOutline, contentDescription = "Empty Star", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp)) }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = villa.rating.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = "  •  $reviewCount reviews", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Property Facilities", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "See all", fontSize = 15.sp, color = MainCyan, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onNavigateToAllFacilities() })
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp), contentPadding = PaddingValues(horizontal = 24.dp), modifier = Modifier.fillMaxWidth()) {
                    items(facilities) { facility ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
                            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                                Icon(imageVector = facility.second, contentDescription = facility.first, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = facility.first, fontSize = 14.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(text = "Description", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    var isDescriptionExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = villa.description, fontSize = 16.sp, color = Color.Gray, lineHeight = 26.sp,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4, overflow = TextOverflow.Ellipsis
                    )

                    if (!isDescriptionExpanded && villa.description.length > 120) {
                        Text(
                            text = "Read more", color = MainCyan, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 6.dp).clickable { isDescriptionExpanded = true }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 56.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)).clickable { onBackClick() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)).clickable { }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        if (showCollectionSheet) {
            val collections = favoriteState.collections
            val favoritesCount = favoriteState.favorites.groupBy { it.collectionId }.mapValues { it.value.size }

            ModalBottomSheet(
                onDismissRequest = { showCollectionSheet = false },
                containerColor = Color.White,
                dragHandle = null
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.size(24.dp))
                        Text(text = "Add to Favorite Collection", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        IconButton(onClick = { showCollectionSheet = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black) }
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 8.dp))

                    if (collections.isEmpty()) {
                        Text(text = "You don't have any collections yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        collections.forEach { collection ->
                            val count = favoritesCount[collection.id] ?: 0
                            val isSavedHere = savedInCollections.contains(collection.id)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        favoriteViewModel.toggleFavoriteInCollection(userId, villa.id, collection.id, isSavedHere)
                                    }
                                    .padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0E0E0))) {
                                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray, modifier = Modifier.align(Alignment.Center))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = collection.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "$count Properties", fontSize = 13.sp, color = Color.Gray)
                                }

                                if (isSavedHere) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Saved", tint = MainCyan)
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Not Saved", tint = Color(0xFFE0E0E0))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showCollectionSheet = false
                            showCreateCollectionSheet = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text(text = "Create New Collection", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }

        if (showCreateCollectionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCreateCollectionSheet = false },
                containerColor = Color.White,
                dragHandle = null
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.size(24.dp))
                        Text(text = "Create New Collection", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        IconButton(onClick = { showCreateCollectionSheet = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black) }
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 16.dp))

                    Text(text = "Name", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newCollectionName, onValueChange = { newCollectionName = it },
                        placeholder = { Text("Enter your collection name", color = Color.Gray, fontSize = 15.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainCyan, unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    val isCreateEnabled = newCollectionName.isNotBlank()
                    Button(
                        onClick = {
                            favoriteViewModel.createCollection(userId, newCollectionName)
                            newCollectionName = ""
                            showCreateCollectionSheet = false
                            showCollectionSheet = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCreateEnabled) MainCyan else Color(0xFFEEEEEE),
                            contentColor = if (isCreateEnabled) Color.White else Color.Gray
                        ),
                        shape = RoundedCornerShape(32.dp), enabled = isCreateEnabled,
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text(text = "Create", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }

        if (selectedImageForFullscreen != null) {
            Dialog(
                onDismissRequest = { selectedImageForFullscreen = null },
                properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { selectedImageForFullscreen = null }
                ) {
                    AsyncImage(
                        model = getResId(selectedImageForFullscreen!!),
                        contentDescription = "Blurred Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(radius = 40.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )

                    AsyncImage(
                        model = getResId(selectedImageForFullscreen!!),
                        contentDescription = "Enlarged Villa Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.9f)
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }
    }
}