package com.example.lokastay.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.lokastay.viewmodel.DetailViewModel
import com.example.lokastay.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
fun BookingScreen(
    villaId: Int,
    initialCheckIn: String = "",
    initialCheckOut: String = "",
    initialGuests: Int = 0,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onNavigateToPayment: () -> Unit = {},
    detailViewModel: DetailViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val detailState by detailViewModel.uiState.collectAsState()
    val transactionState by transactionViewModel.uiState.collectAsState()

    LaunchedEffect(villaId) {
        detailViewModel.loadDetail(villaId)
        transactionViewModel.loadBookedDates(villaId)
    }

    val bookedDateRanges = remember(transactionState.bookedTransactions) {
        transactionState.bookedTransactions.mapNotNull {
            try {
                val start = LocalDate.parse(it.checkInDate)
                val end = LocalDate.parse(it.checkOutDate)
                Pair(start, end)
            } catch (e: Exception) {
                null
            }
        }
    }

    val villa = detailState.villa
    val maxGuest = villa?.maxGuest ?: 2

    var checkInDateStr by remember { mutableStateOf(transactionState.draftCheckIn.ifBlank { if (initialCheckIn == "Any") "" else initialCheckIn }) }
    var checkOutDateStr by remember { mutableStateOf(transactionState.draftCheckOut.ifBlank { if (initialCheckOut == "Any") "" else initialCheckOut }) }
    var currentGuests by remember { mutableIntStateOf(if (transactionState.draftGuests > 0) transactionState.draftGuests else if (initialGuests > 0) initialGuests else 0) }
    var selectedRequests by remember { mutableStateOf(transactionState.draftRequests) }

    LaunchedEffect(checkInDateStr, checkOutDateStr, currentGuests, selectedRequests) {
        transactionViewModel.saveDraftBooking(checkInDateStr, checkOutDateStr, currentGuests, selectedRequests)
    }

    var showRequestSheet by remember { mutableStateOf(false) }
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    val parsedCheckIn = remember(checkInDateStr) { if (checkInDateStr.isNotBlank()) LocalDate.parse(checkInDateStr) else null }
    val parsedCheckOut = remember(checkOutDateStr) { if (checkOutDateStr.isNotBlank()) LocalDate.parse(checkOutDateStr) else null }

    val isGuestExceeded = currentGuests > maxGuest
    val isDateError = parsedCheckIn != null && parsedCheckOut != null && !parsedCheckOut.isAfter(parsedCheckIn)

    val hasOverlap = remember(parsedCheckIn, parsedCheckOut, bookedDateRanges) {
        if (parsedCheckIn != null && parsedCheckOut != null) {
            bookedDateRanges.any { (start, end) ->
                parsedCheckIn.isBefore(end) && parsedCheckOut.isAfter(start)
            }
        } else false
    }

    val isFormComplete = checkInDateStr.isNotBlank() && checkOutDateStr.isNotBlank() && currentGuests > 0 && !isGuestExceeded && !isDateError && !hasOverlap

    val isGuestInfoComplete = transactionState.draftName.isNotBlank() &&
            transactionState.draftEmail.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(transactionState.draftEmail).matches() &&
            transactionState.draftPhone.isNotBlank()

    val canGoToPayment = isFormComplete && isGuestInfoComplete

    if (villa == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MainCyan)
        }
        return
    }

    val context = LocalContext.current
    val imageResId = remember(villa.imageUrl) { context.resources.getIdentifier(villa.imageUrl, "drawable", context.packageName) }
    val formattedPrice = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(villa.pricePerNight.toLong())

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 32.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp, bottom = 32.dp)) {
                    Button(
                        onClick = onNextClick,
                        enabled = isFormComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainCyan,
                            disabledContainerColor = Color(0xFFEEEEEE),
                            disabledContentColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text(text = "Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }

                    Text(
                        text = "Booking and Payment",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 24.dp).verticalScroll(rememberScrollState())) {

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MainCyan), contentAlignment = Alignment.Center) {
                            Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Booking", color = MainCyan, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = Color(0xFFEEEEEE))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = isFormComplete) { onNextClick() }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Text("2", color = if (isFormComplete) Color.Gray else Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guest Info", color = if (isFormComplete) Color.Gray else Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = Color(0xFFEEEEEE))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = canGoToPayment) { onNavigateToPayment() }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Text("3", color = if (canGoToPayment) Color.Gray else Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Payment", color = if (canGoToPayment) Color.Gray else Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = imageResId,
                        contentDescription = villa.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = villa.name,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = villa.location,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "Rp $formattedPrice",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MainCyan
                                )
                                Text(text = "/night", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = villa.rating.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Check in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box {
                            OutlinedTextField(
                                value = parsedCheckIn?.format(formatter) ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Select date", color = Color.Gray, fontSize = 16.sp) },
                                leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFEEEEEE),
                                    focusedBorderColor = MainCyan,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { showCheckInPicker = true })
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Check out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box {
                            OutlinedTextField(
                                value = parsedCheckOut?.format(formatter) ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Select date", color = Color.Gray, fontSize = 16.sp) },
                                leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = if (isDateError || hasOverlap) Color.Red else Color(0xFFEEEEEE),
                                    focusedBorderColor = if (isDateError || hasOverlap) Color.Red else MainCyan,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { showCheckOutPicker = true })
                        }
                    }
                }

                if (isDateError) {
                    Text(text = "Check-out must be after Check-in", color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                } else if (hasOverlap) {
                    Text(text = "Your selected dates include already booked dates.", color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Guest", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(
                            1.dp,
                            if (isGuestExceeded) Color.Red else Color(0xFFEEEEEE),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.PersonOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (currentGuests > 0) "$currentGuests" else "Select guest amount",
                                color = if (currentGuests > 0) Color.Black else Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (currentGuests > 0) MainCyan.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable(enabled = currentGuests > 0) { currentGuests-- },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Remove, tint = if (currentGuests > 0) MainCyan else Color.LightGray, modifier = Modifier.size(18.dp), contentDescription = "Decrease")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MainCyan.copy(alpha = 0.1f))
                                    .clickable { currentGuests++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, tint = MainCyan, modifier = Modifier.size(18.dp), contentDescription = "Increase")
                            }
                        }
                    }
                }

                if (isGuestExceeded) {
                    Text(text = "Maximum guest allowed is $maxGuest", color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Additional Request", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = if (selectedRequests.isEmpty()) "" else selectedRequests.joinToString(", "),
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Add request", color = Color.Gray, fontSize = 16.sp) },
                        trailingIcon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = MainCyan,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showRequestSheet = true })
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showCheckInPicker) {
        LokaStaySingleDatePicker(
            initialDate = parsedCheckIn,
            title = "Select Check-in Date",
            bookedRanges = bookedDateRanges,
            onDismiss = { showCheckInPicker = false },
            onConfirm = { date ->
                checkInDateStr = date.toString()
                showCheckInPicker = false
            }
        )
    }

    if (showCheckOutPicker) {
        LokaStaySingleDatePicker(
            initialDate = parsedCheckOut,
            title = "Select Check-out Date",
            minDate = parsedCheckIn?.plusDays(1) ?: LocalDate.now(),
            bookedRanges = bookedDateRanges,
            onDismiss = { showCheckOutPicker = false },
            onConfirm = { date ->
                checkOutDateStr = date.toString()
                showCheckOutPicker = false
            }
        )
    }

    if (showRequestSheet) {
        val requestOptions = listOf(
            "Extra Bed / Mattress",
            "Floating Breakfast",
            "Airport Pick-up / Drop-off",
            "Early Check-in",
            "Late Check-out",
            "Baby Cot",
            "Honeymoon Decoration"
        )
        var tempSelected by remember { mutableStateOf(selectedRequests) }

        ModalBottomSheet(
            onDismissRequest = { showRequestSheet = false },
            containerColor = Color.White,
            dragHandle = null
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(text = "Additional Request", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = { showRequestSheet = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black) }
                }

                HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 8.dp))

                requestOptions.forEach { option ->
                    val isChecked = tempSelected.contains(option)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempSelected = if (isChecked) tempSelected - option else tempSelected + option
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                tempSelected = if (checked) tempSelected + option else tempSelected - option
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MainCyan, uncheckedColor = Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 16.sp, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        selectedRequests = tempSelected
                        showRequestSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainCyan),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text(text = "Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LokaStaySingleDatePicker(
    initialDate: LocalDate?,
    title: String,
    minDate: LocalDate = LocalDate.now(),
    bookedRanges: List<Pair<LocalDate, LocalDate>> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(initialDate ?: minDate)) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                val isPrevMonthEnabled = currentMonth.isAfter(YearMonth.from(minDate))
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }, enabled = isPrevMonthEnabled) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev", tint = if (isPrevMonthEnabled) Color.Black else Color.LightGray)
                }
                Text(text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = Color.Black)
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

            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(220.dp), userScrollEnabled = false) {
                items(offset + daysInMonth) { index ->
                    if (index >= offset) {
                        val day = index - offset + 1
                        val date = currentMonth.atDay(day)

                        val isPastDate = date.isBefore(minDate)
                        val isSelected = date == selectedDate

                        val isBooked = bookedRanges.any { (start, end) ->
                            !date.isBefore(start) && !date.isAfter(end)
                        }

                        val bgColor = if (isSelected) MainCyan else Color.Transparent
                        val textColor = when {
                            isSelected -> Color.White
                            isPastDate || isBooked -> Color.LightGray
                            else -> Color.Black
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(bgColor)
                                .then(
                                    if (!isPastDate && !isBooked) Modifier.clickable { selectedDate = date } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isBooked) {
                                Icon(Icons.Outlined.EventBusy, contentDescription = "Booked", tint = Color.LightGray, modifier = Modifier.size(16.dp).alpha(0.5f))
                            } else {
                                Text(text = day.toString(), color = textColor, fontSize = 14.sp)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { selectedDate?.let { onConfirm(it) } },
                enabled = selectedDate != null,
                colors = ButtonDefaults.buttonColors(containerColor = MainCyan, disabledContainerColor = Color(0xFFEEEEEE)),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = "Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}