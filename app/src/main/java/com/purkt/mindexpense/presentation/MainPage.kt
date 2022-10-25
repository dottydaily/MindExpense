package com.purkt.mindexpense.presentation

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.purkt.mindexpense.expense.presentation.navigation.addExpenseListTopLevel
import com.purkt.mindexpense.monthly.presentation.navgraph.addMonthlyListTopLevel
import com.purkt.mindexpense.settings.presentation.navgraph.addSettingTopLevel
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainPage(
    navController: NavHostController,
    navigationBarItems: List<NavGraphRoute>
) {
    val navigator = remember { Navigator(navController) }
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.primary
            ) {
                // When backstack is changing, then this state will tell this
                // Bottom navigation bar to recompose again
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navigationBarItems.forEach { graph ->
                    BottomNavigationItem(
                        icon = {
                            Icon(graph.icon, contentDescription = null)
                        },
                        label = {
                            Text(text = stringResource(id = graph.resourceId))
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == graph.route } == true,
                        onClick = {
                            navigator.navigateTo(graph) {
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
        AnimatedNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = NavGraphRoute.Expense.route
        ) {
            addExpenseListTopLevel(navigator)
            addMonthlyListTopLevel(navigator)
            addSettingTopLevel(navigator)
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMainPage() {
    MindExpenseTheme {
        MainPage(
            navController = rememberNavController(),
            navigationBarItems = listOf(
                NavGraphRoute.Expense,
                NavGraphRoute.Setting
            )
        )
    }
}
