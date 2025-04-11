package com.example.mybudgetapp.ui.appbars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController){
    val backStackEntry = navController.currentBackStackEntryAsState()

    // Define bottom navigation tabs with labels, icons, and routes
    val tabs = listOf(
        TabItem("Budget", Icons.AutoMirrored.Filled.ReceiptLong, route = "home"),
        TabItem("Outflow", Icons.Filled.AttachMoney, route = "outflow"),
        TabItem("Insights", Icons.Filled.BarChart, route = "insights"),
        TabItem("Info", Icons.Filled.Info, route = "info"),
        TabItem("Profile", Icons.Filled.Person, route = "profile")
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary, // Set background color of the bottom navigation bar
    ) {
        tabs.forEach { tab ->
            val selected = tab.route ===
                    backStackEntry.value?.destination?.route // Check if tab is selected
            NavigationBarItem(
                selected = selected,
                onClick = {navController.navigate(tab.route)},
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(0.dp) // Remove default padding here
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (selected) Color(0xFF64B5F6) else MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                label = {
                    Text(
                        tab.label,
                        color = if (selected) Color(0xFF64B5F6) else MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(top = 5.dp) // You can adjust the top padding here for text
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFF2EFE7) // Remove the gray highlight background
                )

            )
        }
    }
}