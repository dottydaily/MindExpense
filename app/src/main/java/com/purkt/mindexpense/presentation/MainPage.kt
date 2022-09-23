package com.purkt.mindexpense.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.purkt.mindexpense.expense.presentation.navgraph.addExpenseListTopLevel
import com.purkt.navigation.domain.model.Screen
import com.purkt.setting.presentation.navgraph.addSettingTopLevel

@Composable
fun MainPage(
    navController: NavHostController,
    navigationBarItems: List<Screen>
) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                // When backstack is changing, then this state will tell this
                // Bottom navigation bar to recompose again
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navigationBarItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(Icons.Filled.Favorite, contentDescription = null)
                        },
                        label = {
                            Text(text = stringResource(id = screen.resourceId))
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination to clear all remaining backstack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Prevent from creating same destination when selecting same item
                                launchSingleTop = true
                                // Restore state when selecting on the previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Expense.route
        ) {
            addExpenseListTopLevel(navController)
            addSettingTopLevel(navController)
        }
    }
}