package com.example.featurememo.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

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
