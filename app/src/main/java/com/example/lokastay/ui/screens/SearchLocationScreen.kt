package com.example.lokastay.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokastay.R
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.VillaViewModel

@Composable
fun SearchLocationScreen(
    onBackClick: () -> Unit,
    onLocationSelected: (String) -> Unit,
    villaViewModel: VillaViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val villaState by villaViewModel.uiState.collectAsState()
    val allVillas = villaState.villas

    val dynamicLocations = remember(allVillas) {
        val extractedRegions = allVillas.flatMap { villa ->
            val parts = villa.location.split(",").map { it.trim() }

            val cityAndProvince = parts.takeLast(2)

            cityAndProvince.map {
                it.replace("Kabupaten ", "")
                    .replace("Kota ", "")
                    .replace("DI ", "")
            }
        }

        val extraKeywords = listOf("Puncak", "Bromo", "Lembang", "Anyer", "Dieng", "Seminyak", "Kuta", "Ubud", "Trawas", "Ciater", "Guci")

        (extractedRegions + extraKeywords).distinct().sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainCyan,
                    unfocusedBorderColor = MainCyan,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                placeholder = {
                    Text(text = "Enter your destination", color = Color.Gray, fontSize = 16.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            onLocationSelected(searchQuery.trim())
                        }
                    }
                )
            )
        }

        if (searchQuery.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Image(
                    painter = painterResource(id = R.drawable.img_no_results),
                    contentDescription = "Search Illustration",
                    modifier = Modifier.size(280.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Where are you going?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Search for a city, region, or villa name",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLocationSelected(searchQuery.trim()) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MainCyan.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MainCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Search for \"$searchQuery\"",
                            fontSize = 16.sp,
                            color = MainCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                }

                val filteredLocations = dynamicLocations.filter { it.contains(searchQuery, ignoreCase = true) }

                if (filteredLocations.isNotEmpty()) {
                    item {
                        Text(
                            text = "Location",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                        )
                    }

                    items(filteredLocations) { location ->
                        SearchResultItem(
                            icon = Icons.Outlined.LocationOn,
                            text = location,
                            query = searchQuery,
                            onClick = { onLocationSelected(location) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                val filteredVillas = allVillas.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.location.contains(searchQuery, ignoreCase = true)
                }

                if (filteredVillas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Villas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    items(filteredVillas) { villa ->
                        SearchResultItem(
                            icon = Icons.Outlined.Domain,
                            text = villa.name,
                            query = searchQuery,
                            onClick = { onLocationSelected(villa.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, query: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        val annotatedString = buildAnnotatedString {
            val startIndex = text.indexOf(query, ignoreCase = true)
            if (startIndex >= 0) {
                withStyle(style = SpanStyle(color = MainCyan, fontWeight = FontWeight.Bold)) {
                    append(text.substring(startIndex, startIndex + query.length))
                }
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append(text.substring(startIndex + query.length))
                }
            } else {
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append(text)
                }
            }
        }

        Text(text = annotatedString, fontSize = 16.sp)
    }
}