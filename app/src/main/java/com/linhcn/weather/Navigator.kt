package com.linhcn.weather

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.linhcn.weather.ui.screen.WeatherScreen

object NavTarget {
    const val WEATHER_SCREEN = "weather"
}

@Composable
fun NavigationComponent(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = NavTarget.WEATHER_SCREEN) {
        composable(NavTarget.WEATHER_SCREEN) {
            WeatherScreen(navHostController)
        }
    }
}