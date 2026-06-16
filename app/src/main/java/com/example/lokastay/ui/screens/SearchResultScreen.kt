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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lokastay.R
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.FavoriteViewModel
import com.example.lokastay.viewmodel.VillaViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    locationQuery: String,
    checkInDate: String,
    checkOutDate: String,
    guestCount: Int,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onVillaClick: (Int) -> Unit,
    villaViewModel: VillaViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val villaState by villaViewModel.uiState.collectAsState()
    val favoriteState by favoriteViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val userId = authState.loggedInUserId

    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var selectedRating by rememberSaveable { mutableFloatStateOf(0f) }
    var minPriceInput by rememberSaveable { mutableStateOf("") }
    var maxPriceInput by rememberSaveable { mutableStateOf("") }
    var reqWifi by rememberSaveable { mutableStateOf(false) }
    var reqPool by rememberSaveable { mutableStateOf(false) }
    var reqAc by rememberSaveable { mutableStateOf(false) }
    var reqLaundry by rememberSaveable { mutableStateOf(false) }
    var reqTv by rememberSaveable { mutableStateOf(false) }
    var reqGym by rememberSaveable { mutableStateOf(false) }
    var reqHeater by rememberSaveable { mutableStateOf(false) }
    var reqParking by rememberSaveable { mutableStateOf(false) }
    var reqBbq by rememberSaveable { mutableStateOf(false) }
    var reqBathtub by rememberSaveable { mutableStateOf(false) }
    var reqGarden by rememberSaveable { mutableStateOf(false) }
    var reqKitchen by rememberSaveable { mutableStateOf(false) }

    var showCollectionSheet by remember { mutableStateOf(false) }
    var showCreateCollectionSheet by remember { mutableStateOf(false) }
    var selectedVillaForFav by remember { mutableStateOf<Int?>(null) }
    var newCollectionName by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId != -1) {
            favoriteViewModel.loadFavorites(userId)
            favoriteViewModel.loadCollections(userId)
        }
    }

    LaunchedEffect(
        locationQuery, checkInDate, checkOutDate, guestCount, selectedRating, minPriceInput, maxPriceInput,
        reqWifi, reqPool, reqAc, reqLaundry, reqTv, reqGym, reqHeater, reqParking, reqBbq, reqBathtub, reqGarden, reqKitchen
    ) {
        val loc = if (locationQuery == "All" || locationQuery.isBlank()) "" else locationQuery
        val minP = minPriceInput.toDoubleOrNull() ?: 0.0
        val maxP = maxPriceInput.toDoubleOrNull() ?: 10000000.0

        villaViewModel.filterVilla(
            location = loc,
            guestCount = guestCount,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            rating = selectedRating,
            minPrice = minP,
            maxPrice = maxP,
            requireWifi = reqWifi,
            requirePool = reqPool,
            requireAc = reqAc,
            requireLaundry = reqLaundry,
            requireTv = reqTv,
            requireGym = reqGym,
            requireHeater = reqHeater,
            requireParking = reqParking,
            requireBbq = reqBbq,
            requireBathtub = reqBathtub,
            requireGarden = reqGarden,
            requireKitchen = reqKitchen
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding(),
        containerColor = Color.White
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() },
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.5.dp, MainCyan),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clickable { onBackClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))

                        val locText = if (locationQuery == "All" || locationQuery.isBlank()) "Anywhere" else locationQuery

                        Text(
                            text = locText,
                            fontSize = 16.sp,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showFilterSheet = true }
                )
            }

            if (villaState.villas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Image(
                        painter = painterResource(id = R.drawable.img_no_results),
                        contentDescription = "No Results",
                        modifier = Modifier.size(280.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "No Results",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Please use another keyword",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${villaState.villas.size} villas found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Text(text = "Relevance", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(villaState.villas) { villa ->
                        val isFav = favoriteState.favorites.any { it.villaId == villa.id }

                        SearchResultVillaCard(
                            name = villa.name,
                            location = villa.location,
                            pricePerNight = villa.pricePerNight,
                            rating = villa.rating,
                            imageUrl = villa.imageUrl,
                            isFavorite = isFav,
                            onFavoriteClick = {
                                if (userId != -1) {
                                    selectedVillaForFav = villa.id
                                    showCollectionSheet = true
                                }
                            },
                            onClick = { onVillaClick(villa.id) }
                        )
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                initialRating = selectedRating,
                initialMinPrice = minPriceInput,
                initialMaxPrice = maxPriceInput,
                initialWifi = reqWifi, initialPool = reqPool, initialAc = reqAc, initialLaundry = reqLaundry,
                initialTv = reqTv, initialGym = reqGym, initialHeater = reqHeater, initialParking = reqParking,
                initialBbq = reqBbq, initialBathtub = reqBathtub, initialGarden = reqGarden, initialKitchen = reqKitchen,
                onDismiss = { showFilterSheet = false },
                onApply = { rating, min, max, wifi, pool, ac, laundry, tv, gym, heater, parking, bbq, bathtub, garden, kitchen ->
                    selectedRating = rating
                    minPriceInput = min
                    maxPriceInput = max
                    reqWifi = wifi; reqPool = pool; reqAc = ac; reqLaundry = laundry
                    reqTv = tv; reqGym = gym; reqHeater = heater; reqParking = parking
                    reqBbq = bbq; reqBathtub = bathtub; reqGarden = garden; reqKitchen = kitchen
                    showFilterSheet = false
                }
            )
        }

        if (showCollectionSheet && selectedVillaForFav != null) {
            val collections = favoriteState.collections
            val favoritesCount = favoriteState.favorites.groupBy { it.collectionId }.mapValues { it.value.size }
            val savedInCollections = favoriteState.favorites.filter { it.villaId == selectedVillaForFav }.map { it.collectionId }

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
                                        favoriteViewModel.toggleFavoriteInCollection(userId, selectedVillaForFav!!, collection.id, isSavedHere)
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
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    initialRating: Float,
    initialMinPrice: String,
    initialMaxPrice: String,
    initialWifi: Boolean, initialPool: Boolean, initialAc: Boolean, initialLaundry: Boolean,
    initialTv: Boolean, initialGym: Boolean, initialHeater: Boolean, initialParking: Boolean,
    initialBbq: Boolean, initialBathtub: Boolean, initialGarden: Boolean, initialKitchen: Boolean,
    onDismiss: () -> Unit,
    onApply: (
        rating: Float, minPrice: String, maxPrice: String,
        wifi: Boolean, pool: Boolean, ac: Boolean, laundry: Boolean,
        tv: Boolean, gym: Boolean, heater: Boolean, parking: Boolean,
        bbq: Boolean, bathtub: Boolean, garden: Boolean, kitchen: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tempRating by remember { mutableFloatStateOf(initialRating) }
    var tempMinPrice by remember { mutableStateOf(initialMinPrice) }
    var tempMaxPrice by remember { mutableStateOf(initialMaxPrice) }
    var priceError by remember { mutableStateOf("") }

    var wifi by remember { mutableStateOf(initialWifi) }
    var pool by remember { mutableStateOf(initialPool) }
    var ac by remember { mutableStateOf(initialAc) }
    var laundry by remember { mutableStateOf(initialLaundry) }
    var tv by remember { mutableStateOf(initialTv) }
    var gym by remember { mutableStateOf(initialGym) }
    var heater by remember { mutableStateOf(initialHeater) }
    var parking by remember { mutableStateOf(initialParking) }

    var showMoreFacilities by remember { mutableStateOf(false) }
    var bbq by remember { mutableStateOf(initialBbq) }
    var bathtub by remember { mutableStateOf(initialBathtub) }
    var garden by remember { mutableStateOf(initialGarden) }
    var kitchen by remember { mutableStateOf(initialKitchen) }

    val formatRupiah: (String) -> String = { input ->
        if (input.isEmpty()) "" else {
            val parsed = input.toLongOrNull() ?: 0L
            NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(parsed)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(24.dp))
                Text(text = "Filter", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Ratings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (5 downTo 1).forEach { rating ->
                    val isSelected = tempRating == rating.toFloat()
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, if (isSelected) MainCyan else Color(0xFFEEEEEE)),
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clickable { tempRating = if (isSelected) 0f else rating.toFloat() }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = rating.toString(),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MainCyan else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Price", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Minimum", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formatRupiah(tempMinPrice),
                        onValueChange = { newVal ->
                            val clean = newVal.replace(Regex("[^\\d]"), "")
                            if (clean.length <= 12) tempMinPrice = clean
                            priceError = ""
                        },
                        leadingIcon = { Text("Rp", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainCyan,
                            unfocusedBorderColor = if (priceError.isNotEmpty()) Color.Red else Color(0xFFEEEEEE),
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Maximum", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formatRupiah(tempMaxPrice),
                        onValueChange = { newVal ->
                            val clean = newVal.replace(Regex("[^\\d]"), "")
                            if (clean.length <= 12) tempMaxPrice = clean
                            priceError = ""
                        },
                        leadingIcon = { Text("Rp", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainCyan,
                            unfocusedBorderColor = if (priceError.isNotEmpty()) Color.Red else Color(0xFFEEEEEE),
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                }
            }

            if (priceError.isNotEmpty()) {
                Text(text = priceError, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, start = 4.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Facilities", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    FacilityCheckbox("Free Wifi", wifi) { wifi = it }
                    FacilityCheckbox("Swimming Pool", pool) { pool = it }
                    FacilityCheckbox("Air Conditioner", ac) { ac = it }
                    FacilityCheckbox("Laundry", laundry) { laundry = it }

                    if (showMoreFacilities) {
                        FacilityCheckbox("BBQ Area", bbq) { bbq = it }
                        FacilityCheckbox("Bathtub", bathtub) { bathtub = it }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    FacilityCheckbox("Television", tv) { tv = it }
                    FacilityCheckbox("Gym Center", gym) { gym = it }
                    FacilityCheckbox("Water Heater", heater) { heater = it }
                    FacilityCheckbox("Free Parking", parking) { parking = it }

                    if (showMoreFacilities) {
                        FacilityCheckbox("Garden", garden) { garden = it }
                        FacilityCheckbox("Kitchen", kitchen) { kitchen = it }
                    }
                }
            }

            Text(
                text = if (showMoreFacilities) "See less ⌃" else "See more ⌄",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { showMoreFacilities = !showMoreFacilities }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val minVal = tempMinPrice.toLongOrNull() ?: 0L
                    val maxVal = tempMaxPrice.toLongOrNull() ?: 0L

                    if (tempMaxPrice.isNotEmpty() && minVal > maxVal) {
                        priceError = "Minimum price cannot be greater than maximum price."
                    } else {
                        priceError = ""
                        onApply(
                            tempRating, tempMinPrice, tempMaxPrice,
                            wifi, pool, ac, laundry, tv, gym, heater, parking, bbq, bathtub, garden, kitchen
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(text = "Apply", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FacilityCheckbox(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { onCheckedChange(!isChecked) }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MainCyan, uncheckedColor = Color.LightGray)
        )
        Text(text = label, fontSize = 16.sp, color = Color.Gray)
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun SearchResultVillaCard(
    name: String, location: String, pricePerNight: Double, rating: Float, imageUrl: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(imageUrl) {
        context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
    }
    val formattedPrice = remember(pricePerNight) {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(pricePerNight.toLong())
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box {
                AsyncImage(
                    model = imageResId,
                    contentDescription = "Villa Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onFavoriteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE53935) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = location, fontSize = 13.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "Rp $formattedPrice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MainCyan)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = rating.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}