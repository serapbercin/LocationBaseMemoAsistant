package com.example.featurememo.ui

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
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.featurememo.CreateMemoViewModel2
import com.example.featurememo.EditMemoViewModel
import com.example.featurememo.LocalMemoRepository
import com.example.featurememo.MemoRoutes
import com.example.featurememo.createMemoVMFactory
import com.example.featurememo.editMemoVMFactory
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemoScreen(
    nav: NavController,
    memoId: Long,
) {

    val repo = LocalMemoRepository.current

    // Build the VM using the provided repo
    val vm: EditMemoViewModel = viewModel(
        factory = editMemoVMFactory(repo)
    )

    val ui by vm.ui.collectAsState()

    // Load existing memo once
    LaunchedEffect(memoId) { vm.load(memoId) }

    // Listen for location result returned from MapPickerScreen
    val savedStateHandle = nav.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow<LatLng?>("picked_location", null)
            ?.collect { loc ->
                loc?.let { vm.onLocation(it) }
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit Memo") }) },
        floatingActionButton = {
            val canSave = ui.isValid()
            ExtendedFloatingActionButton(
                text = { Text("Save") },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                onClick = {
                    if (!canSave) return@ExtendedFloatingActionButton
                    vm.save { /* savedId -> register geofence if needed */ }
                    nav.popBackStack() // back to Home
                },
                expanded = true,
                modifier = Modifier
                    .alpha(if (canSave) 1f else 0.4f)
                    .semantics { if (!canSave) disabled() } // accessibility hint
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
                onValueChange = vm::onTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.description,
                onValueChange = vm::onDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = { nav.navigate(MemoRoutes.MAP) }) {
                Text(if (ui.location != null) "Change Location ✓" else "Pick Location on Map")
            }
        }
    }
}
