package com.example.lokastay.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.DetailViewModel
import com.example.lokastay.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun EReceiptScreen(
    villaId: Int,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    detailViewModel: DetailViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val detailState by detailViewModel.uiState.collectAsState()
    val transactionState by transactionViewModel.uiState.collectAsState()

    LaunchedEffect(villaId) {
        detailViewModel.loadDetail(villaId)
    }

    val villa = detailState.villa
    if (villa == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MainCyan)
        }
        return
    }

    val formatCurrency: (Double) -> String = { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(it.toLong()) }

    val parsedCheckIn = LocalDate.parse(transactionState.draftCheckIn.ifBlank { LocalDate.now().toString() })
    val parsedCheckOut = LocalDate.parse(transactionState.draftCheckOut.ifBlank { LocalDate.now().plusDays(1).toString() })
    val totalNights = ChronoUnit.DAYS.between(parsedCheckIn, parsedCheckOut).toInt().coerceAtLeast(1)

    val baseAmount = villa.pricePerNight * totalNights
    val taxAmount = baseAmount * 0.10
    val totalAmount = transactionState.totalPrice
    val discountAmount = (baseAmount + taxAmount) - totalAmount

    // Kalkulasi poin yang didapatkan dari total transaksi
    val pointsEarned = (totalAmount / 10000).toInt()

    Scaffold(containerColor = Color(0xFFF5F5F5)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Surface(
                color = Color(0xFFF5F5F5),
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
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }

                    Text(
                        text = "E-Receipt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(48.dp)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = villa.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = villa.location,
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Check in", fontSize = 13.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(parsedCheckIn.format(DateTimeFormatter.ofPattern("EEE, dd MMM")), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("13:00", fontSize = 13.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = MainCyan, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$totalNights Nights", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Check out", fontSize = 13.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(parsedCheckOut.format(DateTimeFormatter.ofPattern("EEE, dd MMM")), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("14:00", fontSize = 13.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Guests", fontSize = 14.sp, color = Color.Gray)
                                Text("${transactionState.draftGuests} Guests", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Guest Info", fontSize = 14.sp, color = Color.Gray)
                                Text(transactionState.draftName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount", fontSize = 14.sp, color = Color.Gray)
                                Text("Rp ${formatCurrency(baseAmount)}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tax (10%)", fontSize = 14.sp, color = Color.Gray)
                                Text("Rp ${formatCurrency(taxAmount)}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                            }
                            if (discountAmount > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Discount", fontSize = 14.sp, color = Color.Gray)
                                    Text("- Rp ${formatCurrency(discountAmount)}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                Text("Rp ${formatCurrency(totalAmount)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MainCyan)
                            }

                            if (pointsEarned > 0) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MainCyan.copy(alpha = 0.1f))
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Yay! You earned $pointsEarned Points",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MainCyan
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.size(24.dp).offset(x = (-12).dp).clip(CircleShape).background(Color(0xFFF5F5F5)))
                            Canvas(modifier = Modifier.weight(1f).height(2.dp)) {
                                drawLine(
                                    color = Color(0xFFEEEEEE), start = Offset(0f, 0f), end = Offset(size.width, 0f),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                )
                            }
                            Box(modifier = Modifier.size(24.dp).offset(x = 12.dp).clip(CircleShape).background(Color(0xFFF5F5F5)))
                        }

                        Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
                            val widths = listOf(3, 5, 2, 4, 2, 1, 6, 2, 3, 2, 5, 1, 3, 2, 4, 1, 2, 5, 3, 2, 1, 4, 2)
                            Row(modifier = Modifier.fillMaxWidth().height(56.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                for (i in 0..45) {
                                    Box(modifier = Modifier.width(widths[i % widths.size].dp).fillMaxHeight().background(Color.Black))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.5.dp, MainCyan)
                    ) {
                        Text("Back to Home", color = MainCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainCyan)
                    ) {
                        Text("Download", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}