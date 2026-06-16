package com.example.lokastay.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLocation: String = "",
    villaViewModel: VillaViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    onNavigateToSearchLocation: () -> Unit = {},
    onNavigateToSearchResults: (String, String, String, Int) -> Unit = { _, _, _, _ -> },
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToBooking: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onVillaClick: (Int) -> Unit = {}
) {
    val villaState by villaViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val favoriteState by favoriteViewModel.uiState.collectAsState()

    LaunchedEffect(authState.loggedInUserId) {
        if (authState.loggedInUserId != -1) {
            favoriteViewModel.loadFavorites(authState.loggedInUserId)
        }
    }

    val favoriteVillaIds = remember(favoriteState.favorites) {
        favoriteState.favorites.map { it.villaId }.toSet()
    }

    val popularVillas = remember(villaState.villas) {
        villaState.villas
            .sortedByDescending { it.rating }
            .take(5)
    }

    val userName = authState.userName.ifBlank { "Guest" }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    var selectedStartDateStr by rememberSaveable { mutableStateOf("") }
    var selectedEndDateStr by rememberSaveable { mutableStateOf("") }

    var guestCount by rememberSaveable { mutableIntStateOf(1) }

    val selectedStartDate = remember(selectedStartDateStr) {
        if (selectedStartDateStr.isNotBlank()) LocalDate.parse(selectedStartDateStr) else null
    }
    val selectedEndDate = remember(selectedEndDateStr) {
        if (selectedEndDateStr.isNotBlank()) LocalDate.parse(selectedEndDateStr) else null
    }

    val datePillText = remember(selectedStartDate, selectedEndDate) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        when {
            selectedStartDate != null && selectedEndDate != null ->
                "${selectedStartDate.format(formatter)} - ${selectedEndDate.format(formatter)}"
            selectedStartDate != null ->
                selectedStartDate.format(formatter)
            else -> ""
        }
    }

    Scaffold(
        bottomBar = {
            LokastayBottomNavigationBar(
                currentTab = "home",
                onNavigateToHome = { },
                onNavigateToFavorite = onNavigateToFavorite,
                onNavigateToBooking = onNavigateToBooking,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_wave),
                contentDescription = "Wave Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 26.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Hi $userName \uD83D\uDC4B", fontSize = 15.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Let's start your journey!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notification",
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935))
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        CustomSearchField(
                            label = "Location",
                            placeholder = "Enter your destination",
                            value = selectedLocation,
                            icon = Icons.Outlined.LocationOn,
                            onClick = { onNavigateToSearchLocation() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomSearchField(
                                label = "Date",
                                placeholder = "Select Date",
                                value = datePillText,
                                icon = Icons.Outlined.CalendarToday,
                                modifier = Modifier.weight(1.3f),
                                onClick = { showDatePicker = true }
                            )

                            GuestCounterField(
                                label = "Guest",
                                count = guestCount,
                                onIncrement = { guestCount++ },
                                onDecrement = { if (guestCount > 1) guestCount-- },
                                modifier = Modifier.weight(0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val locToSend = selectedLocation.ifBlank { "All" }
                                val checkInStr = selectedStartDateStr
                                val checkOutStr = selectedEndDateStr
                                onNavigateToSearchResults(locToSend, checkInStr, checkOutStr, guestCount)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(text = "Search", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Popular Villa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "See all", fontSize = 14.sp, color = MainCyan, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { })
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(popularVillas) { villa ->
                        VillaCard(
                            name = villa.name,
                            location = villa.location,
                            pricePerNight = villa.pricePerNight,
                            rating = villa.rating,
                            imageUrl = villa.imageUrl,
                            isFavorite = favoriteVillaIds.contains(villa.id),
                            onClick = { onVillaClick(villa.id) }
                        )
                    }
                }
            }
        }

        if (showDatePicker) {
            LokaStayDateRangePicker(
                onDismiss = { showDatePicker = false },
                onConfirm = { start, end ->
                    selectedStartDateStr = start?.toString() ?: ""
                    selectedEndDateStr = end?.toString() ?: ""
                    showDatePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LokaStayDateRangePicker(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate?, LocalDate?) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { YearMonth.now() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isPrevMonthEnabled = currentMonth.isAfter(currentYearMonth)

                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) },
                    enabled = isPrevMonthEnabled
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = if (isPrevMonthEnabled) Color.Black else Color.LightGray
                    )
                }

                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                daysOfWeek.forEach { day ->
                    Text(text = day, fontSize = 12.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfMonth = currentMonth.atDay(1)
            val offset = if (firstDayOfMonth.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.value

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(220.dp),
                userScrollEnabled = false
            ) {
                items(offset + daysInMonth) { index ->
                    if (index >= offset) {
                        val day = index - offset + 1
                        val date = currentMonth.atDay(day)

                        val isPastDate = date.isBefore(today)

                        val isSelectedStart = date == startDate
                        val isSelectedEnd = date == endDate
                        val isInRange = startDate != null && endDate != null && date.isAfter(startDate) && date.isBefore(endDate)

                        val bgColor = when {
                            isSelectedStart || isSelectedEnd -> MainCyan
                            isInRange -> MainCyan.copy(alpha = 0.1f)
                            else -> Color.Transparent
                        }

                        val textColor = when {
                            isSelectedStart || isSelectedEnd -> Color.White
                            isPastDate -> Color.LightGray
                            else -> Color.Black
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(if (isSelectedStart || isSelectedEnd) CircleShape else RoundedCornerShape(0.dp))
                                .background(bgColor)
                                .then(
                                    if (!isPastDate) {
                                        Modifier.clickable {
                                            if (startDate == null || (startDate != null && endDate != null)) {
                                                startDate = date
                                                endDate = null
                                            } else if (date.isBefore(startDate)) {
                                                startDate = date
                                            } else {
                                                endDate = date
                                            }
                                        }
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = day.toString(), color = textColor, fontSize = 14.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Check in", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = startDate?.format(formatter) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select date", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFFEEEEEE),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Check out", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = endDate?.format(formatter) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select date", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFFEEEEEE),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(startDate, endDate) },
                colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(text = "Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
fun GuestCounterField(
    label: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFEEEEEE), CircleShape)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (count > 1) MainCyan.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable(enabled = count > 1) { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Remove",
                    tint = if (count > 1) MainCyan else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$count",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MainCyan.copy(alpha = 0.1f))
                    .clickable { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MainCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun LokastayBottomNavigationBar(
    currentTab: String = "home",
    onNavigateToHome: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToBooking: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.shadow(24.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", modifier = Modifier.size(24.dp)) },
            label = { Text("Home", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = currentTab == "home",
            onClick = { if (currentTab != "home") onNavigateToHome() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MainCyan,
                selectedTextColor = MainCyan,
                indicatorColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentTab == "favorite") Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Favorite", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = currentTab == "favorite",
            onClick = { if (currentTab != "favorite") onNavigateToFavorite() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MainCyan,
                selectedTextColor = MainCyan,
                indicatorColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Outlined.EventNote, contentDescription = "Booking", modifier = Modifier.size(24.dp)) },
            label = { Text("Booking", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = currentTab == "booking",
            onClick = { if (currentTab != "booking") onNavigateToBooking() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MainCyan,
                selectedTextColor = MainCyan,
                indicatorColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.PersonOutline, contentDescription = "Profile", modifier = Modifier.size(24.dp)) },
            label = { Text("Profile", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = currentTab == "profile",
            onClick = { if (currentTab != "profile") onNavigateToProfile() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MainCyan,
                selectedTextColor = MainCyan,
                indicatorColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

@Composable
fun CustomSearchField(
    label: String,
    placeholder: String,
    icon: ImageVector,
    value: String = "",
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                placeholder = { Text(text = placeholder, color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = MainCyan,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )

            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onClick() }
                )
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun VillaCard(
    name: String,
    location: String,
    pricePerNight: Double,
    rating: Float,
    imageUrl: String,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {}
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
        modifier = Modifier.width(240.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box {
                AsyncImage(
                    model = imageResId,
                    contentDescription = "Villa Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .border(1.5.dp, if (isFavorite) Color.White else Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE53935) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = location, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "Rp $formattedPrice", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MainCyan)
                    Text(text = "/night", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}