package com.example.lokastay.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Payment
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
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.DetailViewModel
import com.example.lokastay.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class Promo(val title: String, val discountPercent: Double, val discountFixed: Double, val expDate: String)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
fun PaymentScreen(
    villaId: Int,
    onBackClick: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onNavigateToGuestInfo: () -> Unit,
    onPaymentComplete: (Boolean) -> Unit,
    detailViewModel: DetailViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val detailState by detailViewModel.uiState.collectAsState()
    val transactionState by transactionViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(villaId) {
        detailViewModel.loadDetail(villaId)
    }

    LaunchedEffect(authState.loggedInUserId) {
        if (authState.loggedInUserId != -1) {
            transactionViewModel.loadUserPoints(authState.loggedInUserId)
        }
    }

    val villa = detailState.villa
    if (villa == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MainCyan)
        }
        return
    }

    val parsedCheckIn = remember(transactionState.draftCheckIn) {
        if (transactionState.draftCheckIn.isNotBlank()) LocalDate.parse(transactionState.draftCheckIn) else LocalDate.now()
    }
    val parsedCheckOut = remember(transactionState.draftCheckOut) {
        if (transactionState.draftCheckOut.isNotBlank()) LocalDate.parse(transactionState.draftCheckOut) else LocalDate.now().plusDays(1)
    }
    val totalNights = ChronoUnit.DAYS.between(parsedCheckIn, parsedCheckOut).toInt().coerceAtLeast(1)

    val baseAmount = villa.pricePerNight * totalNights
    val taxAmount = baseAmount * 0.10

    var showPaymentSheet by remember { mutableStateOf(false) }
    var showPromoSheet by remember { mutableStateOf(false) }
    var usePoints by remember { mutableStateOf(false) }

    val promos = remember {
        listOf(
            Promo("10% Discount", 0.10, 0.0, "30 Jun 2026"),
            Promo("20% Discount", 0.20, 0.0, "15 Jul 2026"),
            Promo("Rp 50.000 Cashback", 0.0, 50000.0, "20 Jul 2026"),
            Promo("Rp 20.000 Discount", 0.0, 20000.0, "10 Aug 2026")
        )
    }

    var selectedPaymentMethod by remember { mutableStateOf<String?>(transactionState.draftPaymentMethod.ifBlank { null }) }
    var selectedPromo by remember { mutableStateOf<Promo?>(promos.find { it.title == transactionState.draftPromoTitle }) }

    LaunchedEffect(selectedPaymentMethod, selectedPromo) {
        transactionViewModel.saveDraftPayment(
            method = selectedPaymentMethod ?: "",
            promoTitle = selectedPromo?.title ?: ""
        )
    }

    val discountAmount = selectedPromo?.let { promo ->
        if (promo.discountPercent > 0) baseAmount * promo.discountPercent else promo.discountFixed
    } ?: 0.0

    val subTotalBeforePoints = (baseAmount + taxAmount - discountAmount).coerceAtLeast(0.0)

    val maxPointsDiscount = transactionState.availablePoints * 100.0
    val pointsDeduction = if (usePoints && transactionState.availablePoints > 0) {
        if (maxPointsDiscount >= subTotalBeforePoints) subTotalBeforePoints else maxPointsDiscount
    } else 0.0

    val finalTotal = (subTotalBeforePoints - pointsDeduction).coerceAtLeast(0.0)

    val formatCurrency: (Double) -> String = { amount ->
        NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(amount.toLong())
    }

    val context = LocalContext.current
    val imageResId = remember(villa.imageUrl) { context.resources.getIdentifier(villa.imageUrl, "drawable", context.packageName) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 32.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Total", fontSize = 14.sp, color = Color.Gray)
                        Text(text = "Rp ${formatCurrency(finalTotal)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Button(
                        onClick = {
                            val isSuccess = selectedPaymentMethod != "Pay at Villa"
                            transactionViewModel.createTransaction(
                                userId = authState.loggedInUserId,
                                villaId = villaId,
                                checkInDate = transactionState.draftCheckIn,
                                checkOutDate = transactionState.draftCheckOut,
                                nights = totalNights,
                                isSuccess = isSuccess,
                                usePoints = usePoints
                            )
                            onPaymentComplete(isSuccess)
                        },
                        enabled = selectedPaymentMethod != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainCyan,
                            disabledContainerColor = Color(0xFFEEEEEE),
                            disabledContentColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.width(150.dp).height(54.dp)
                    ) {
                        Text(text = "Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
        ) {

            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().zIndex(1f)
            ) {
                Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 4.dp, bottom = 16.dp)) {
                    Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(48.dp).clickable { onBackClick() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                    Text(text = "Booking and Payment", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.align(Alignment.Center))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onNavigateToBooking() }.padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
                        Text("1", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Booking", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = Color(0xFFEEEEEE))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onNavigateToGuestInfo() }.padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
                        Text("2", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guest Info", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = Color(0xFFEEEEEE))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MainCyan), contentAlignment = Alignment.Center) {
                        Text("3", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment", color = MainCyan, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = imageResId, contentDescription = villa.name, contentScale = ContentScale.Crop,
                        modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = villa.name, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = villa.location, fontSize = 14.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(text = "Rp ${formatCurrency(villa.pricePerNight)}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MainCyan)
                                Text(text = "/night", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = villa.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Check in", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(parsedCheckIn.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("13:00", fontSize = 14.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.DarkMode, contentDescription = "Nights", tint = MainCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$totalNights Nights", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Check out", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(parsedCheckOut.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("14:00", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Guests", fontSize = 16.sp, color = Color.Gray)
                    Text("${transactionState.draftGuests} Guests", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Guest Info", fontSize = 16.sp, color = Color.Gray)
                    Text(transactionState.draftName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showPaymentSheet = true }.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Payment, contentDescription = "Payment", tint = MainCyan)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(selectedPaymentMethod ?: "Choose payment method", fontSize = 16.sp, color = if (selectedPaymentMethod != null) Color.Black else Color.Gray)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Arrow", tint = Color.Gray)
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))

                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showPromoSheet = true }.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = "Promo", tint = MainCyan)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(selectedPromo?.title ?: "Add promo", fontSize = 16.sp, color = if (selectedPromo != null) Color.Black else Color.Gray)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Arrow", tint = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(24.dp))

                if (transactionState.availablePoints > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFFF8E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Star, contentDescription = "Points", tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Use ${transactionState.availablePoints} Points", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Save Rp ${formatCurrency(maxPointsDiscount)}", fontSize = 13.sp, color = MainCyan, fontWeight = FontWeight.Medium)
                            }
                        }
                        Switch(
                            checked = usePoints && transactionState.availablePoints > 0,
                            onCheckedChange = { usePoints = it },
                            enabled = transactionState.availablePoints > 0,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MainCyan,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFE0E0E0),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amount", fontSize = 16.sp, color = Color.Gray)
                    Text("Rp ${formatCurrency(baseAmount)}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (10%)", fontSize = 16.sp, color = Color.Gray)
                    Text("Rp ${formatCurrency(taxAmount)}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                }
                if (selectedPromo != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Discount", fontSize = 16.sp, color = MainCyan)
                        Text("- Rp ${formatCurrency(discountAmount)}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MainCyan)
                    }
                }
                if (usePoints && pointsDeduction > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Points Redeemed", fontSize = 16.sp, color = Color(0xFFFFC107))
                        Text("- Rp ${formatCurrency(pointsDeduction)}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFFC107))
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    if (showPaymentSheet) {
        val methods = listOf("Bank Transfer (Virtual Account)", "Credit / Debit Card", "GoPay / OVO / Dana", "PayPal", "Pay at Villa")
        val (tempMethod, setTempMethod) = remember { mutableStateOf(selectedPaymentMethod) }

        ModalBottomSheet(onDismissRequest = { showPaymentSheet = false }, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text("Select Method Payment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = { showPaymentSheet = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black) }
                }
                Spacer(modifier = Modifier.height(16.dp))

                methods.forEach { method ->
                    val isSelected = tempMethod == method
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, if (isSelected) MainCyan else Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { setTempMethod(method) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(method, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = Color.Black)
                        RadioButton(
                            selected = isSelected,
                            onClick = { setTempMethod(method) },
                            colors = RadioButtonDefaults.colors(selectedColor = MainCyan, unselectedColor = Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        selectedPaymentMethod = tempMethod
                        showPaymentSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }

    if (showPromoSheet) {
        val (tempPromo, setTempPromo) = remember { mutableStateOf(selectedPromo) }

        ModalBottomSheet(onDismissRequest = { showPromoSheet = false }, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text("Add Promo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = { showPromoSheet = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black) }
                }
                Spacer(modifier = Modifier.height(16.dp))

                promos.forEach { promo ->
                    val isSelected = tempPromo == promo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, if (isSelected) MainCyan else Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { setTempPromo(promo) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = MainCyan)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(promo.title, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = Color.Black)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Expired in ${promo.expDate}", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { setTempPromo(promo) },
                            colors = RadioButtonDefaults.colors(selectedColor = MainCyan, unselectedColor = Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        selectedPromo = tempPromo
                        showPromoSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}