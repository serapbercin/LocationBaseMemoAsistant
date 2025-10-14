package com.example.featurememo.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.featurememo.CreateMemoViewModel2
import com.example.featurememo.LocalMemoRepository
import com.example.featurememo.MemoRoutes
import com.example.featurememo.createMemoVMFactory
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemoScreen(
    nav: NavController,
    onRegisterGeofence: (savedId: Long, latLng: LatLng) -> Unit = { _, _ -> }
) {
// Get the IMemoRepository provided by :app
    val repo = LocalMemoRepository.current

    // Build the VM using the provided repo
    val vm: CreateMemoViewModel2 = viewModel(
        factory = createMemoVMFactory(repo)
    )

    val context = LocalContext.current
    val ui by vm.ui.collectAsState()

    // ---- Runtime permission launchers ----
    val reqFine = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op here; we re-check before calling */ }

    val reqBg = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* optional: show rationale if denied */ }

    // Helper checks
    fun hasFine() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    fun hasBg(): Boolean =
        if (Build.VERSION.SDK_INT >= 29)
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        else true

    // Location returned from MapPicker
    val savedStateHandle = nav.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<LatLng?>("picked_location", null)?.collect { loc ->
            loc?.let { vm.updateLocation(it.latitude, it.longitude) }
        }
    }

    val canSave = vm.isValid()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Memo") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Save") },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                onClick = {
                    if (!canSave) return@ExtendedFloatingActionButton

                    // 1) Ensure FINE; if missing, request and exit
                    if (!hasFine()) {
                        reqFine.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        return@ExtendedFloatingActionButton
                    }
                    // 2) (Optional but recommended) ensure BACKGROUND on Android 10+
                    if (Build.VERSION.SDK_INT >= 29 && !hasBg()) {
                        // Requesting BG requires FINE already granted
                        reqBg.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        // We’ll come back here on next tap
                        return@ExtendedFloatingActionButton
                    }

                    // 3) Save memo, then register geofence safely
                    vm.saveMemo { savedId ->
                        Log.d("CreateMemoScreen", "location ${vm.ui.value.location}")
                        val loc = vm.ui.value.location ?: return@saveMemo
                        try {
                            Log.d(
                                "CreateMemoScreen",
                                "Registering geofence for memo $savedId loc: $loc"
                            )
                            onRegisterGeofence(savedId, loc)
                            nav.popBackStack()
                        } catch (se: SecurityException) {
                            Log.d("CreateMemoScreen", "SecurityException: ${se.message}")
                            // Handle gracefully (permission revoked in between, etc.)
                            // e.g., show a Snackbar/Toast explaining permission is required
                        }
                    }
                },
                expanded = true,
                modifier = Modifier
                    .alpha(if (canSave) 1f else 0.4f)
                    .semantics { if (!canSave) disabled() }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui.title, onValueChange = vm::onTitleChanged,
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.description, onValueChange = vm::onDescriptionChanged,
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { nav.navigate(MemoRoutes.MAP) }) {
                Text(if (ui.location != null) "Location Selected ✓" else "Pick Location on Map")
            }
        }
    }
}
