/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import android.provider.ContactsContract
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.LunchTrayAppBar
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum

enum class LunchScreen() {
    Start,
    EntreeMenu,
    SideDishMenu,
    AccompanimentMenu,
    Checkout
}

// TODO: AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp() {
    // TODO: Create Controller and initialization
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = LunchScreen.valueOf(
        backStackEntry?.destination?.route ?: LunchScreen.Start.name
    )

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            // TODO: AppBar
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = LunchScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = LunchScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(LunchScreen.EntreeMenu.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = LunchScreen.EntreeMenu.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchScreen.Start.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchScreen.SideDishMenu.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateEntree(item)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = LunchScreen.SideDishMenu.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchScreen.Start.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchScreen.AccompanimentMenu.name)
                    },
                    onSelectionChanged = { item ->
                            viewModel.updateSideDish(item)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = LunchScreen.AccompanimentMenu.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchScreen.Start.name, inclusive = false)
                    },
                    {
                        navController.navigate(LunchScreen.Checkout.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateAccompaniment(item)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = LunchScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchScreen.Start.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchScreen.Start.name, inclusive = false)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}
