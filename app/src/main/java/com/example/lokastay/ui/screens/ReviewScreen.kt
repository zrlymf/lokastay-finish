package com.example.lokastay.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokastay.data.entity.Review
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.AuthViewModel
import com.example.lokastay.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    villaId: Int,
    onBackClick: () -> Unit,
    detailViewModel: DetailViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val authState by authViewModel.uiState.collectAsState()
    val userName = authState.userName.ifBlank { "Guest" }

    val detailState by detailViewModel.uiState.collectAsState()

    var showAddReviewSheet by remember { mutableStateOf(false) }

    LaunchedEffect(villaId) {
        detailViewModel.loadDetail(villaId)
    }

    val allReviews = detailState.reviews

    val filteredReviews = if (searchQuery.isBlank()) {
        allReviews
    } else {
        allReviews.filter { it.comment.contains(searchQuery, ignoreCase = true) }
    }

    val totalReviewCount = remember(villaId, allReviews.size) {
        val baseFakeCount = when (villaId) {
            1 -> 915; 2 -> 879; 3 -> 750; 4 -> 946; 5 -> 891
            6 -> 879; 7 -> 936; 8 -> 887; 9 -> 792; 10 -> 786
            11 -> 811; 12 -> 927; 13 -> 758; 14 -> 836; 15 -> 779
            else -> 850
        }
        baseFakeCount + allReviews.size
    }

    val subRatings = remember(villaId) {
        val random = java.util.Random(villaId.toLong())
        listOf(
            "Cleanliness" to String.format("%.1f", 4.5f + random.nextFloat() * 0.5f),
            "Services" to String.format("%.1f", 4.4f + random.nextFloat() * 0.6f),
            "Location" to String.format("%.1f", 4.6f + random.nextFloat() * 0.4f),
            "Value" to String.format("%.1f", 4.3f + random.nextFloat() * 0.7f)
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding(),
        containerColor = Color.White,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddReviewSheet = true },
                containerColor = MainCyan,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Create, contentDescription = "Add") },
                text = { Text("Write a Review", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        text = "All Reviews",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$totalReviewCount Reviews",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {  }) {
                            Text(text = "Relevance", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subRatings) { (category, score) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                modifier = Modifier.width(110.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = category, fontSize = 14.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = score, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainCyan,
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        placeholder = {
                            Text(text = "Search review", color = Color.Gray, fontSize = 15.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                itemsIndexed(filteredReviews) { index, review ->
                    ReviewItem(review = review, index = index)
                    HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
        }

        if (showAddReviewSheet) {
            AddReviewBottomSheet(
                onDismiss = { showAddReviewSheet = false },
                onSubmit = { rating, comment ->
                    detailViewModel.addReview(villaId, userName, rating.toFloat(), comment)
                    showAddReviewSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var selectedRating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    val isSubmitEnabled = selectedRating > 0 && reviewText.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(24.dp))
                Text(text = "Write a Review", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 16.dp))

            Text(text = "Tap to Rate", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Rate $i",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { selectedRating = i }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Your Experience", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("Tell us what you liked or disliked...", color = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainCyan,
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedContainerColor = Color(0xFFF9F9F9)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSubmit(selectedRating, reviewText) },
                enabled = isSubmitEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainCyan,
                    disabledContainerColor = Color(0xFFEEEEEE),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text(text = "Submit Review", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
fun ReviewItem(review: Review, index: Int) {
    val displayDate = when (index) {
        0 -> "Just now"
        1 -> "2 days ago"
        2 -> "5 days ago"
        3 -> "1 week ago"
        4 -> "2 weeks ago"
        else -> "A long time ago"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MainCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = review.username, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fullStars = review.rating.toInt()
                    repeat(fullStars) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    }
                    val emptyStars = 5 - fullStars
                    repeat(emptyStars) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEEEEEE), modifier = Modifier.size(14.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = displayDate, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = review.comment,
            fontSize = 15.sp,
            color = Color.DarkGray,
            lineHeight = 24.sp
        )
    }
}