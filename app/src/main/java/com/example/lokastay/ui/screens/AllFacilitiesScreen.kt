package com.example.lokastay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.DetailViewModel

@Composable
fun AllFacilitiesScreen(
    villaId: Int,
    onBackClick: () -> Unit,
    detailViewModel: DetailViewModel = viewModel()
) {
    val uiState by detailViewModel.uiState.collectAsState()
    val villa = uiState.villa

    LaunchedEffect(villaId) {
        detailViewModel.loadDetail(villaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                    text = "All Property Facilities",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (villa != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                val loc = villa.location.lowercase()
                val isBeach = loc.contains("kuta") || loc.contains("seminyak") || loc.contains("anyer") || loc.contains("bali")
                val isMountain = loc.contains("puncak") || loc.contains("bromo") || loc.contains("lembang") || loc.contains("dieng") || loc.contains("batu") || loc.contains("trawas")
                val isHotSpring = loc.contains("ciater") || loc.contains("guci")
                val isForest = loc.contains("ubud") || loc.contains("sentul")
                val isCity = loc.contains("dago") || loc.contains("yogyakarta")

                val hotelServices = mutableListOf("Medical Services", "Money Changer", "Luggage Storage", "Concierge", "24 hour Security", "Bellhop", "Wake-up Call", "Daily Housekeeping")
                if (villa.hasLaundry) hotelServices.add(0, "Laundry")
                ExpandableCategoryGroup("Hotel Service", Icons.Outlined.RoomService, hotelServices, isInitiallyExpanded = true)

                val thingsToDo = mutableListOf<String>()
                if (isBeach) thingsToDo.addAll(listOf("Surfing Lessons", "Snorkeling", "Beach Volleyball", "Sunset Cruise", "Sunbathing Area"))
                else if (isMountain) thingsToDo.addAll(listOf("Sunrise Tour", "Mountain Trekking", "Campfire/Bonfire", "Tea Plantation Walk", "Off-road Jeep"))
                else if (isHotSpring) thingsToDo.addAll(listOf("Thermal Bath", "Spa & Massage", "Sauna", "Nature Walk"))
                else if (isForest) thingsToDo.addAll(listOf("Jungle Trekking", "River Rafting", "Bird Watching", "Yoga Sessions"))
                else if (isCity) thingsToDo.addAll(listOf("City Sightseeing", "Culinary Tour", "Museum Visit", "Shopping Guide"))
                else thingsToDo.addAll(listOf("Board Games", "Library", "Billiard", "Karaoke"))
                ExpandableCategoryGroup("Things to Do", Icons.AutoMirrored.Outlined.FormatListBulleted, thingsToDo)

                val foods = mutableListOf("Room Service", "Breakfast Buffet", "Minibar", "Welcome Drink")
                if (villa.hasBbq) foods.add("BBQ Equipment")
                if (isBeach) foods.addAll(listOf("Seafood BBQ", "Beachfront Dining", "Tropical Cocktails"))
                else if (isMountain || isHotSpring) foods.addAll(listOf("Warm Beverages (Bandrek/Bajigur)", "Roasted Corn", "Local Hot Soup"))
                else if (isForest) foods.addAll(listOf("Organic Farm-to-Table", "Traditional Herbal Drinks"))
                else foods.addAll(listOf("Fine Dining", "Local Heritage Food", "Coffee Shop"))
                ExpandableCategoryGroup("Foods and Drinks", Icons.Outlined.RiceBowl, foods)

                val general = mutableListOf("Non-smoking rooms", "Family Rooms", "Wheelchair Accessible", "Designated Smoking Area")
                if (villa.hasAc) general.add(0, "Air Conditioning")
                if (villa.hasTv) general.add(0, "Flat-screen TV")
                if (villa.hasWifi) general.add(0, "High-speed WiFi")
                if (villa.hasHeater) general.add("Water Heater")
                ExpandableCategoryGroup("General", Icons.Outlined.Settings, general)

                val nearby = mutableListOf("ATM/Banking", "Minimarket", "Pharmacy", "Clinic/Hospital")
                if (isBeach) nearby.addAll(listOf("Beach Clubs", "Surf Shops", "Seafood Market"))
                else if (isMountain) nearby.addAll(listOf("National Park", "Strawberry Farm", "Scenic Viewpoints"))
                else if (isForest) nearby.addAll(listOf("Waterfall Trails", "Eco-park", "Monkey Forest"))
                else if (isCity) nearby.addAll(listOf("Shopping Mall", "Art Galleries", "Historical Sites"))
                ExpandableCategoryGroup("Nearby Facilities", Icons.Outlined.Storefront, nearby)

                val publicFac = mutableListOf("Lobby", "Lounge Area", "Public Restroom", "Prayer Room (Mushola)")
                if (villa.hasPool) publicFac.add(0, "Infinity Pool")
                if (villa.hasBathtub) publicFac.add("Jacuzzi/Bathtub")
                if (villa.hasGarden) publicFac.add("Lush Courtyard/Garden")
                if (villa.hasParking) publicFac.add("Secure Parking Area")
                ExpandableCategoryGroup("Public Facilities", Icons.Outlined.Business, publicFac)

                val sports = mutableListOf("Bicycle Rental")
                if (villa.hasGym) sports.add(0, "Gym Center")
                if (isBeach) sports.addAll(listOf("Jet Ski", "Canoeing"))
                else if (isMountain || isForest) sports.addAll(listOf("ATV Ride", "Mountain Biking", "Archery"))
                else sports.addAll(listOf("Tennis Court", "Table Tennis"))
                ExpandableCategoryGroup("Sports and Recreations", Icons.Outlined.SportsFootball, sports)

                ExpandableCategoryGroup("Transportation", Icons.Outlined.DirectionsCar, listOf("Airport Transfer", "Car Rental with Driver", "Taxi Service", "Valet Parking", "Shuttle Service"))

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ExpandableCategoryGroup(
    title: String,
    icon: ImageVector,
    items: List<String>,
    isInitiallyExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F7FA).copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = MainCyan, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                    val chunkedItems = items.chunked(2)
                    chunkedItems.forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {

                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color.Gray))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = pair[0], fontSize = 16.sp, color = Color.Gray, maxLines = 2)
                            }

                            if (pair.size > 1) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color.Gray))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = pair[1], fontSize = 16.sp, color = Color.Gray, maxLines = 2)
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}