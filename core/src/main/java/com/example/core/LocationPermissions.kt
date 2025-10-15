package com.example.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.core.AndroidPermissions.ACCESS_BACKGROUND_LOCATION
import com.example.core.AndroidPermissions.ACCESS_FINE_LOCATION

/**
 * Utility class to check for location permissions, referencing constants from AndroidPermissions.
 */
object LocationPermissions {

    /**
     * Checks if a specific permission is granted.
     */
    private fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED


    /**
     * Checks if ACCESS_FINE_LOCATION is granted.
     */
    fun hasFine(context: Context): Boolean = isGranted(context, ACCESS_FINE_LOCATION)

    /**
     * Checks if ACCESS_BACKGROUND_LOCATION is granted.
     * On devices older than Android Q (API 29), background location is covered by fine location,
     * so this returns true.
     */
    fun hasBackground(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isGranted(context, ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }

    /**
     * Returns a list of missing permissions required for foreground location (ACCESS_FINE_LOCATION only).
     */
    fun missingForForegroundLocation(context: Context): List<String> =
        if (hasFine(context)) emptyList() else listOf(ACCESS_FINE_LOCATION)

    /**
     * Returns a list of missing permissions required for Geofencing/Background location.
     * This requires ACCESS_FINE_LOCATION on all versions, and ACCESS_BACKGROUND_LOCATION
     * on Android Q (API 29) and later.
     */
    fun missingForGeofencing(context: Context, requireBackground: Boolean = true): List<String> {
        val missing = mutableListOf<String>()
        if (!hasFine(context)) missing += ACCESS_FINE_LOCATION
        if (requireBackground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!hasBackground(context)) missing += ACCESS_BACKGROUND_LOCATION
        }
        return missing
    }
}