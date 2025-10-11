package com.sap.codelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sap.codelab.ui.CreateMemoScreen
import com.sap.codelab.ui.EditMemoScreen
import com.sap.codelab.ui.HomeScreen
import com.sap.codelab.ui.MapPickerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MemoApp() }
    }
}

@Composable
fun MemoApp() {
    val nav = rememberNavController()
    MaterialTheme {
        NavHost(navController = nav, startDestination = "home") {
            composable("home") { HomeScreen(nav) }
            composable("create") { CreateMemoScreen(nav) }
            composable("map") { MapPickerScreen(nav) } // used for both create/edit
            composable("edit/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")!!.toLong()
                EditMemoScreen(nav, id)
            }
        }
    }
}