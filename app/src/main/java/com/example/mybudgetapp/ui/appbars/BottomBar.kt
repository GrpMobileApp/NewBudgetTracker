package com.example.mybudgetapp.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
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
    val tabs = listOf(
        TabItem("Budget", Icons.Filled.ReceiptLong, route = "home"),
        TabItem("Transaction", Icons.Filled.AttachMoney, route = "transaction"),
        TabItem("Insights", Icons.Filled.BarChart, route = "insights"),
        TabItem("Profile", Icons.Filled.Person, route = "profile")
    )
    NavigationBar(
        containerColor = Color.DarkGray
    ) {
        tabs.forEach { tab ->
            val selected = tab.route ===
                    backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {navController.navigate(tab.route)},
                label = {
                    Text(
                        tab.label,
                        color = if (selected) Color(0xFF64B5F6) else Color.LightGray
                    )
                },
                icon = { Icon(
                    imageVector = tab.icon,
                    contentDescription = null,
                    tint = if (selected) Color(0xFF64B5F6) else Color.LightGray
                ) }
            )
        }
    }
}