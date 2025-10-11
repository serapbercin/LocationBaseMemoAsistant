package com.sap.codelab.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.sap.codelab.CreateMemoViewModel2

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    nav: NavController,
) {

    var picked by remember { mutableStateOf<LatLng?>(null) }

    Scaffold(
        floatingActionButton = {
            picked?.let { chosen ->
                ExtendedFloatingActionButton(
                    text = { Text("Confirm Location") },
                    icon = { Icon(Icons.Default.Check, null) },
                    onClick = {
                        // Send result to the previous entry (create OR edit)
                        nav.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("picked_location", chosen)
                        nav.popBackStack()
                    }
                )
            }
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier.padding(padding),
            onMapClick = { latLng -> picked = latLng }
        ) {
            picked?.let { Marker(state = MarkerState(it), title = "Selected") }
        }
    }
}
