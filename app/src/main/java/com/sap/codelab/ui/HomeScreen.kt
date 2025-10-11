package com.sap.codelab.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.Repository
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavController) {
    val memos = remember { mutableStateListOf<Memo>() }

    // Collect the list reactively
    LaunchedEffect(Unit) {
        Repository.observeAll().collectLatest { list ->
            memos.clear()
            memos.addAll(list)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Memos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("create") }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        if (memos.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No memos yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(memos) { memo ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { nav.navigate("edit/${memo.id}") }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(memo.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                memo.description,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
