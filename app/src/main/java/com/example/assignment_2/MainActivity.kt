package com.example.assignment_2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment_2.ui.components.PriceTag
import com.example.assignment_2.ui.screens.BasketScreen
import com.example.assignment_2.ui.screens.BrowseScreen
import com.example.assignment_2.ui.screens.HistoryScreen
import com.example.assignment_2.ui.theme.Assignment_2Theme
import com.example.assignment_2.viewmodel.BookingViewModel

/** The three screens inside MainActivity, used as Navigation Compose routes. */
object Routes {
    const val BROWSE = "browse"
    const val BASKET = "basket"
    const val HISTORY = "history"
}

// launchSingleTop stops the same screen being stacked twice if its button is tapped again.
private fun NavController.goTo(route: String) = navigate(route) { launchSingleTop = true }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Assignment_2Theme { BeerHireApp() } }
    }
}

/** The whole Main UI: top bar, Snackbars and the NavHost holding Browse, Basket and History. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeerHireApp(vm: BookingViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val route = navController.currentBackStackEntryAsState().value?.destination?.route

    // Launches BookingActivity; the lambda runs when it finishes and hands back its result.
    val bookingLauncher = rememberLauncherForActivityResult(BookingContract()) { result ->
        Log.d("MainActivity", "Result from BookingActivity: $result")
        vm.applyBooking(result)
    }

    // Show each one-off message as a Snackbar, then clear it so it doesn't show again.
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHost.showSnackbar(it)
            vm.messageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beer Hire") },
                navigationIcon = {
                    if (route != Routes.BROWSE) {
                        TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                    }
                },
                actions = {
                    PriceTag(state.credits, Modifier.padding(end = 4.dp))      // balance, visible on every screen
                    TextButton(onClick = { navController.goTo(Routes.BASKET) }) { Text("Basket") }
                    TextButton(onClick = { navController.goTo(Routes.HISTORY) }) { Text("History") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        // Every screen gets the same vm, so they all share one basket, balance and history.
        NavHost(navController, startDestination = Routes.BROWSE, modifier = Modifier.padding(padding)) {
            composable(Routes.BROWSE) { BrowseScreen(vm) }
            composable(Routes.BASKET) {
                BasketScreen(vm, onBook = { request -> bookingLauncher.launch(request) })
            }
            composable(Routes.HISTORY) { HistoryScreen(vm) }
        }
    }
}
