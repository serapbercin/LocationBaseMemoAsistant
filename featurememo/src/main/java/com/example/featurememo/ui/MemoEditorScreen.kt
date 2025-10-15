package com.example.featurememo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.core.LocationPermissions
import com.example.featurememo.EditorMode
import com.example.featurememo.Effect
import com.example.featurememo.MemoEditorUi
import com.example.featurememo.MemoEditorViewModel
import com.example.featurememo.MemoRoutes
import com.example.featurememo.R
import com.example.featurememo.di.LocalViewModelFactory
import com.example.featurememo.ui.common.UiSpacing
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoEditorScreen(
    nav: NavController,
    memoId: Long?,
    onRegisterGeofence: (savedId: Long, latLng: LatLng) -> Unit = { _, _ -> }
) {
    val factory = LocalViewModelFactory.current
    val vm: MemoEditorViewModel = viewModel(factory = factory)
    LaunchedEffect(memoId) { vm.initialize(memoId) }

    val ui by vm.uiStateFlow.collectAsState()

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val context = LocalContext.current

    ObservePickedLocation(nav) { vm.onLocationChanged(it) }

    val actions = rememberEditorActions(nav, vm, onRegisterGeofence)

    LaunchedEffect(Unit) {
        vm.effects.collect { eff ->
            when (eff) {
                is Effect.ShowMessage ->
                    snackbarHostState.showSnackbar(message = context.getString(eff.resId))
                is Effect.CloseScreen ->
                    nav.popBackStack()
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    EditorScaffold(
        isCreate = ui.mode is EditorMode.Create,
        canSubmit = vm.isValid(),
        onSubmit = actions.submitWithPermissions,
        onClose = { nav.popBackStack() },
        snackbarHostState = snackbarHostState,
        content = { contentPadding ->
            EditorForm(
                ui = ui,
                onTitle = vm::onTitleChanged,
                onDescription = vm::onDescriptionChanged,
                onPickLocation = { nav.navigate(MemoRoutes.MAP) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .imePadding()
                    .navigationBarsPadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    )
}


@Composable
private fun ObservePickedLocation(
    nav: NavController,
    onPicked: (LatLng) -> Unit
) {
    val saved = nav.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(saved) {
        saved?.getStateFlow<LatLng?>(NavKeys.PICKED_LOCATION, null)?.collect { it?.let(onPicked) }
    }
}

@Composable
private fun rememberEditorActions(
    nav: NavController,
    vm: MemoEditorViewModel,
    onRegisterGeofence: (Long, LatLng) -> Unit
): EditorActions {
    val ctx = LocalContext.current

    val reqFine = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) vmSubmit(vm, nav, onRegisterGeofence) else Unit
    }

    fun submitWithPermissions() {
        val missing =
            LocationPermissions.missingForGeofencing(context = ctx, requireBackground = false)
        if (missing.isNotEmpty()) {
            reqFine.launch(missing.first())
            return
        }
        vmSubmit(vm, nav, onRegisterGeofence)
    }

    return remember { EditorActions(::submitWithPermissions) }
}

private fun vmSubmit(
    vm: MemoEditorViewModel,
    nav: NavController,
    onRegisterGeofence: (Long, LatLng) -> Unit
) {
    vm.submit(
        onCreated = { id ->
            vm.uiStateFlow.value.location?.let { onRegisterGeofence(id, it) }
            CoroutineScope(Dispatchers.Main).launch { nav.popBackStack() }
        },
        onUpdated = {
            CoroutineScope(Dispatchers.Main).launch { nav.popBackStack() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorScaffold(
    isCreate: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isCreate)
                            stringResource(R.string.title_new_memo)
                        else
                            stringResource(R.string.title_edit_memo)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.cta_save)) },
                icon = { Icon(Icons.Default.Check, null) },
                onClick = { if (canSubmit) onSubmit() },
                expanded = true,
                modifier = Modifier
                    .alpha(if (canSubmit) 1f else 0.4f)
                    .semantics { if (!canSubmit) disabled() }
            )
        },
        content = content
    )
}


@Composable
private fun EditorForm(
    ui: MemoEditorUi,
    onTitle: (String) -> Unit,
    onDescription: (String) -> Unit,
    onPickLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(UiSpacing.md),
        verticalArrangement = Arrangement.spacedBy(UiSpacing.md)
    ) {
        OutlinedTextField(
            value = ui.title,
            onValueChange = onTitle,
            label = {
                Text(
                    stringResource(R.string.label_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = ui.description,
            onValueChange = onDescription,
            label = { Text(stringResource(R.string.label_description)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 8
        )
        Button(onClick = onPickLocation) {
            Text(
                text = if (ui.location != null)
                    stringResource(R.string.status_location_selected)
                else
                    stringResource(R.string.cta_pick_location)
            )
        }
    }
}


private class EditorActions(val submitWithPermissions: () -> Unit)
