package com.sap.codelab.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.sap.codelab.CreateMemoViewModel2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemoScreen(
    nav: NavController,
    vm: CreateMemoViewModel2 = viewModel()
) {
    val ui by vm.ui.collectAsState()

    // Listen for location result returned from MapPickerScreen
    val savedStateHandle = nav.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow<LatLng?>("picked_location", null)
            ?.collect { loc ->
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
                    vm.saveMemo { nav.popBackStack() }
                },
                expanded = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (canSave) 1f else 0.4f)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui.title,
                onValueChange = vm::onTitleChanged,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.description,
                onValueChange = vm::onDescriptionChanged,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = { nav.navigate("map") }) {
                Text(if (ui.location != null) "Location Selected ✓" else "Pick Location on Map")
            }
        }
    }
}
