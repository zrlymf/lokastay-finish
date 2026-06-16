package com.example.lokastay.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.lokastay.data.entity.Transaction
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.TransactionViewModel
import com.example.lokastay.viewmodel.VillaViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MyBookingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onRebookClick: (Int) -> Unit,
    onAddReviewClick: (Int) -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    villaViewModel: VillaViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val transactionState by transactionViewModel.uiState.collectAsState()
    val villaState by villaViewModel.uiState.collectAsState()

    LaunchedEffect(authState.loggedInUserId) {
        villaViewModel.loadAllVillas()
        transactionViewModel.loadUserTransactions(authState.loggedInUserId)
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Completed", "Cancelled")

    var showCancelDialog by remember { mutableStateOf(false) }
    var transactionToCancel by remember { mutableStateOf<Int?>(null) }

    val today = LocalDate.now()

    val upcomingList = transactionState.transactions.filter {
        it.paymentStatus == "SUCCESS" && runCatching { LocalDate.parse(it.checkOutDate) >= today }.getOrDefault(false)
    }.sortedBy { it.checkInDate }

    val completedList = transactionState.transactions.filter {
        it.paymentStatus == "SUCCESS" && runCatching { LocalDate.parse(it.checkOutDate) < today }.getOrDefault(false)
    }.sortedByDescending { it.checkOutDate }

    val cancelledList = transactionState.transactions.filter {
        it.paymentStatus == "CANCELLED" || it.paymentStatus == "FAILED"
    }.sortedByDescending { it.id }

    val currentList = when (selectedTabIndex) {
        0 -> upcomingList
        1 -> completedList
        else -> cancelledList
    }

    Scaffold(
        bottomBar = {
            LokastayBottomNavigationBar(
                currentTab = "booking",
                onNavigateToHome = onNavigateToHome,
                onNavigateToFavorite = onNavigateToFavorite,
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
                modifier = Modifier.fillMaxWidth().zIndex(1f)
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
                        text = "My Booking",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = MainCyan,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MainCyan,
                            height = 3.dp
                        )
                    }
                },
                divider = { }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) MainCyan else Color.Gray
                            )
                        }
                    )
                }
            }

            if (currentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No bookings found in this category.", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(currentList) { transaction ->
                        val villa = villaState.villas.find { it.id == transaction.villaId }
                        if (villa != null) {
                            BookingCard(
                                transaction = transaction,
                                villaName = villa.name,
                                location = villa.location,
                                imageUrl = villa.imageUrl,
                                tabIndex = selectedTabIndex,
                                onCancelClick = {
                                    transactionToCancel = transaction.id
                                    showCancelDialog = true
                                },
                                onRebookClick = { onRebookClick(villa.id) },
                                onAddReviewClick = { onAddReviewClick(villa.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showCancelDialog && transactionToCancel != null) {
            AlertDialog(
                onDismissRequest = {
                    showCancelDialog = false
                    transactionToCancel = null
                },
                containerColor = Color.White,
                title = {
                    Text(text = "Cancel Booking", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                },
                text = {
                    Text(
                        text = "Are you sure you want to cancel this booking? This action cannot be undone.",
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            transactionViewModel.cancelTransaction(transactionToCancel!!, authState.loggedInUserId)
                            showCancelDialog = false
                            transactionToCancel = null
                        }
                    ) {
                        Text("Yes, Cancel", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCancelDialog = false
                            transactionToCancel = null
                        }
                    ) {
                        Text("No, Keep it", color = MainCyan, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun BookingCard(
    transaction: Transaction,
    villaName: String,
    location: String,
    imageUrl: String,
    tabIndex: Int,
    onCancelClick: () -> Unit,
    onRebookClick: () -> Unit,
    onAddReviewClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(imageUrl) { context.resources.getIdentifier(imageUrl, "drawable", context.packageName) }

    val formatCurrency: (Double) -> String = { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(it.toLong()) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    val checkIn = runCatching { LocalDate.parse(transaction.checkInDate) }.getOrNull() ?: LocalDate.now()
    val checkOut = runCatching { LocalDate.parse(transaction.checkOutDate) }.getOrNull() ?: LocalDate.now().plusDays(1)

    val today = LocalDate.now()
    val canCancel = today.isBefore(checkIn)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = imageResId,
                    contentDescription = villaName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = villaName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = location, fontSize = 14.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row {
                    Column {
                        Text(text = checkIn.format(formatter), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Text(text = "Check in", fontSize = 13.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = checkOut.format(formatter), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Text(text = "Check out", fontSize = 13.sp, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Rp ${formatCurrency(transaction.totalPrice)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MainCyan)
                    Text(text = "Total Price", fontSize = 13.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (tabIndex) {
                0 -> {
                    Column {
                        if (canCancel) {
                            OutlinedButton(
                                onClick = onCancelClick,
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.5.dp, Color.Red),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Cancel Booking", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "*Free cancellation is only available until H-1 before check-in.",
                                fontSize = 13.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        } else {
                            OutlinedButton(
                                onClick = { },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.5.dp, Color.LightGray)
                            ) {
                                Text("Cancel Booking", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "*Cannot be cancelled on or after the check-in date.",
                                fontSize = 13.sp, color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                1 -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onRebookClick,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.5.dp, MainCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MainCyan)
                        ) {
                            Text("Re-book", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onAddReviewClick,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MainCyan)
                        ) {
                            Text("Add Review", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    Button(
                        onClick = onRebookClick,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainCyan)
                    ) {
                        Text("Re-book", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}