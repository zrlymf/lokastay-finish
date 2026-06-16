package com.example.lokastay.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lokastay.ui.screens.AllFacilitiesScreen
import com.example.lokastay.ui.screens.BookingScreen
import com.example.lokastay.ui.screens.CollectionDetailScreen
import com.example.lokastay.ui.screens.DetailScreen
import com.example.lokastay.ui.screens.EReceiptScreen
import com.example.lokastay.ui.screens.FavoriteScreen
import com.example.lokastay.ui.screens.GuestInfoScreen
import com.example.lokastay.ui.screens.HomeScreen
import com.example.lokastay.ui.screens.LoginScreen
import com.example.lokastay.ui.screens.MyBookingScreen
import com.example.lokastay.ui.screens.PaymentResultScreen
import com.example.lokastay.ui.screens.PaymentScreen
import com.example.lokastay.ui.screens.ProfileScreen
import com.example.lokastay.ui.screens.RegisterScreen
import com.example.lokastay.ui.screens.ReviewScreen
import com.example.lokastay.ui.screens.SearchLocationScreen
import com.example.lokastay.ui.screens.SearchResultScreen
import com.example.lokastay.ui.screens.SplashScreen
import com.example.lokastay.viewmodel.TransactionViewModel

@Composable
fun LokastayApp() {
    val navController = rememberNavController()

    val sharedTransactionViewModel: TransactionViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") { backStackEntry ->
            val selectedLocation by backStackEntry.savedStateHandle
                .getStateFlow("selected_location", "")
                .collectAsState()

            HomeScreen(
                selectedLocation = selectedLocation,
                onNavigateToSearchLocation = {
                    navController.navigate("search_location")
                },
                onNavigateToSearchResults = { loc, checkIn, checkOut, guests ->
                    val safeLoc = loc.ifEmpty { "All" }
                    val safeCheckIn = checkIn.ifEmpty { "Any" }
                    val safeCheckOut = checkOut.ifEmpty { "Any" }

                    navController.navigate("search_result/$safeLoc/$safeCheckIn/$safeCheckOut/$guests")
                },
                onNavigateToFavorite = {
                    navController.navigate("favorite") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToBooking = {
                    navController.navigate("my_booking") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onVillaClick = { villaId ->
                    navController.navigate("detail/$villaId")
                }
            )
        }

        composable("my_booking") {
            MyBookingScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToFavorite = {
                    navController.navigate("favorite") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onRebookClick = { villaId ->
                    navController.navigate("booking/$villaId/Any/Any/0")
                },
                onAddReviewClick = { villaId ->
                    navController.navigate("reviews/$villaId")
                },
                transactionViewModel = sharedTransactionViewModel
            )
        }

        composable("search_location") {
            SearchLocationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLocationSelected = { location ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_location", location)

                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "search_result/{location}/{checkIn}/{checkOut}/{guests}",
            arguments = listOf(
                navArgument("location") { type = NavType.StringType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("guests") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location") ?: "All"
            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: "Any"
            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: "Any"
            val guests = backStackEntry.arguments?.getInt("guests") ?: 1

            SearchResultScreen(
                locationQuery = location,
                checkInDate = checkIn,
                checkOutDate = checkOut,
                guestCount = guests,
                onBackClick = { navController.popBackStack() },
                onFilterClick = {
                },
                onVillaClick = { villaId ->
                    navController.navigate("detail/$villaId?checkIn=$checkIn&checkOut=$checkOut&guests=$guests")
                }
            )
        }

        composable(
            route = "detail/{villaId}?checkIn={checkIn}&checkOut={checkOut}&guests={guests}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType },
                navArgument("checkIn") { type = NavType.StringType; defaultValue = "Any" },
                navArgument("checkOut") { type = NavType.StringType; defaultValue = "Any" },
                navArgument("guests") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0
            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: "Any"
            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: "Any"
            val guests = backStackEntry.arguments?.getInt("guests") ?: 0

            DetailScreen(
                villaId = villaId,
                checkInDate = if (checkIn == "Any") "" else checkIn,
                checkOutDate = if (checkOut == "Any") "" else checkOut,
                guestCount = guests,
                onBackClick = { navController.popBackStack() },
                onNavigateToAllFacilities = {
                    navController.navigate("all_facilities/$villaId")
                },
                onNavigateToReviews = {
                    navController.navigate("reviews/$villaId")
                },
                onNavigateToBooking = { vId, inDate, outDate, gCount ->
                    val safeCheckIn = inDate.ifEmpty { "Any" }
                    val safeCheckOut = outDate.ifEmpty { "Any" }
                    navController.navigate("booking/$vId/$safeCheckIn/$safeCheckOut/$gCount")
                }
            )
        }

        composable(
            route = "booking/{villaId}/{checkIn}/{checkOut}/{guests}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("guests") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0
            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: "Any"
            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: "Any"
            val guests = backStackEntry.arguments?.getInt("guests") ?: 0

            BookingScreen(
                villaId = villaId,
                initialCheckIn = checkIn,
                initialCheckOut = checkOut,
                initialGuests = guests,
                transactionViewModel = sharedTransactionViewModel,
                onBackClick = { navController.popBackStack() },
                onNextClick = {
                    navController.navigate("guest_info/$villaId")
                },
                onNavigateToPayment = {
                    navController.navigate("payment/$villaId")
                }
            )
        }

        composable(
            route = "guest_info/{villaId}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0

            GuestInfoScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = {
                    navController.navigate("payment/$villaId")
                },
                transactionViewModel = sharedTransactionViewModel
            )
        }

        composable(
            route = "payment/{villaId}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0

            PaymentScreen(
                villaId = villaId,
                transactionViewModel = sharedTransactionViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToBooking = {
                    navController.navigate("booking/$villaId/Any/Any/0") {
                        popUpTo("detail/$villaId") { inclusive = false }
                    }
                },
                onNavigateToGuestInfo = {
                    navController.navigate("guest_info/$villaId") {
                        popUpTo("detail/$villaId") { inclusive = false }
                    }
                },
                onPaymentComplete = { isSuccess ->
                    navController.navigate("payment_result/$isSuccess/$villaId") {
                        popUpTo("detail/$villaId") { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = "payment_result/{isSuccess}/{villaId}",
            arguments = listOf(
                navArgument("isSuccess") { type = NavType.BoolType },
                navArgument("villaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val isSuccess = backStackEntry.arguments?.getBoolean("isSuccess") ?: false
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0

            PaymentResultScreen(
                isSuccess = isSuccess,
                onBackToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onEReceiptClick = {
                    navController.navigate("e_receipt/$villaId")
                },
                onTryAgainClick = {
                    navController.navigate("payment/$villaId") {
                        popUpTo("detail/$villaId") { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = "e_receipt/{villaId}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0
            val context = LocalContext.current

            EReceiptScreen(
                villaId = villaId,
                onBackClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onDownloadClick = {
                    Toast.makeText(context, "E-Receipt Downloaded!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                detailViewModel = viewModel(),
                transactionViewModel = sharedTransactionViewModel
            )
        }

        composable(
            route = "all_facilities/{villaId}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0

            AllFacilitiesScreen(
                villaId = villaId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "reviews/{villaId}",
            arguments = listOf(
                navArgument("villaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getInt("villaId") ?: 0

            ReviewScreen(
                villaId = villaId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("favorite") {
            FavoriteScreen(
                onCollectionClick = { collectionId ->
                    navController.navigate("collection_detail/$collectionId")
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToBooking = {
                    navController.navigate("my_booking") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = "collection_detail/{collectionId}",
            arguments = listOf(
                navArgument("collectionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getInt("collectionId") ?: 0

            CollectionDetailScreen(
                collectionId = collectionId,
                onBackClick = { navController.popBackStack() },
                onVillaClick = { villaId ->
                    navController.navigate("detail/$villaId")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToFavorite = {
                    navController.navigate("favorite") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToBooking = {
                    navController.navigate("my_booking") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onLogoutSuccess = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}