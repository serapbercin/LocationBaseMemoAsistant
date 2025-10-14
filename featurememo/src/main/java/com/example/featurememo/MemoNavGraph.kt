package com.example.featurememo

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.featurememo.ui.CreateMemoScreen
import com.example.featurememo.ui.EditMemoScreen
import com.example.featurememo.ui.HomeScreen
import com.example.featurememo.ui.MapPickerScreen
import com.google.android.gms.maps.model.LatLng

fun NavGraphBuilder.memoNavGraph(
    nav: NavController,
    onRegisterGeofence: (savedId: Long, latLng: LatLng) -> Unit
) {

    composable(MemoRoutes.HOME) { HomeScreen(nav) }
    composable(MemoRoutes.CREATE) { CreateMemoScreen(nav, onRegisterGeofence = onRegisterGeofence) }
    composable(MemoRoutes.MAP) { MapPickerScreen(nav) }

    composable(
        route = MemoRoutes.EDIT,                          // "memo/edit/{id}"
        arguments = listOf(navArgument("id") { type = NavType.LongType })
    ) { backStack ->
        val id = backStack.arguments!!.getLong("id")
        EditMemoScreen(nav, id)
    }
}