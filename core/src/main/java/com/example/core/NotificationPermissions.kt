package com.example.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NotificationPermissions {

    /** Non-null only on API 33+. */
    fun requiredPermissionOrNull(): String? =
        if (Build.VERSION.SDK_INT >= 33) AndroidPermissions.POST_NOTIFICATIONS else null

    /** True if we are allowed to post notifications now. */
    fun canPost(context: Context): Boolean {
        val post = requiredPermissionOrNull() ?: return true
        return ContextCompat.checkSelfPermission(context, post) == PackageManager.PERMISSION_GRANTED
    }

}
