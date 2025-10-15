package com.example.featurememo.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.featurememo.HomeViewModel
import com.example.featurememo.MemoRoutes
import com.example.featurememo.di.LocalViewModelFactory
import com.google.android.gms.maps.model.LatLng

fun NavGraphBuilder.memoNavGraph(
    nav: NavController,
    onRegisterGeofence: (savedId: Long, latLng: LatLng) -> Unit
) {
    composable(MemoRoutes.HOME) { HomeRoute(nav) }

    composable(MemoRoutes.editor()) {
        MemoEditorScreen(nav = nav, memoId = null, onRegisterGeofence = onRegisterGeofence)
    }

    composable(
        route = MemoRoutes.EDITOR,
        arguments = listOf(
            navArgument(MemoRoutes.ARG_ID) {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) { backStack ->
        val raw = backStack.arguments?.getLong(MemoRoutes.ARG_ID, -1L) ?: -1L
        val id = raw.takeIf { it > 0L }
        MemoEditorScreen(nav = nav, memoId = id, onRegisterGeofence = onRegisterGeofence)
    }

    composable(MemoRoutes.MAP) { MapPickerScreen(nav) }
}


@Composable
fun HomeRoute(nav: NavController) {
    val vmFactory = LocalViewModelFactory.current
    val owner = checkNotNull(LocalViewModelStoreOwner.current)
    val vm: HomeViewModel = viewModel(viewModelStoreOwner = owner, factory = vmFactory)

    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.effects.collect { eff ->
            when (eff) {
                is HomeViewModel.Effect.NavigateToEdit -> {
                    if (eff.id == null) nav.navigate(MemoRoutes.editor())
                    else nav.navigate(MemoRoutes.editor(eff.id))
                }

                is HomeViewModel.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(eff.resId)
                    )
                }
            }
        }
    }

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAdd = vm::onAddClicked,
        onOpen = vm::onMemoClicked,
        onDelete = vm::onDeleteClicked
    )
}