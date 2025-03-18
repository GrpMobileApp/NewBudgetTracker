package com.example.mybudgetapp.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController){
    val backStackEntry = navController.currentBackStackEntryAsState()

    // Define bottom navigation tabs with labels, icons, and routes
    val tabs = listOf(
        TabItem("Budget", Icons.Filled.ReceiptLong, route = "home"),
        TabItem("Outflow", Icons.Filled.AttachMoney, route = "outflow"),
        TabItem("Insights", Icons.Filled.BarChart, route = "insights"),
        TabItem("Info", Icons.Filled.Info, route = "info"),
        TabItem("Profile", Icons.Filled.Person, route = "profile")
    )
    NavigationBar(
        containerColor = Color.DarkGray // Set background color of the bottom navigation bar
    ) {
        tabs.forEach { tab ->
            val selected = tab.route ===
                    backStackEntry.value?.destination?.route // Check if tab is selected
            NavigationBarItem(
                selected = selected,
                onClick = {navController.navigate(tab.route)},
                label = {
                    Text(
                        tab.label,
                        color = if (selected) Color(0xFF64B5F6) else Color.LightGray // Change text color if selected
                    )
                },
                icon = { Icon(
                    imageVector = tab.icon,
                    contentDescription = null,
                    tint = if (selected) Color(0xFF64B5F6) else Color.LightGray // Change text color if selected
                ) }
            )
        }
    }
}