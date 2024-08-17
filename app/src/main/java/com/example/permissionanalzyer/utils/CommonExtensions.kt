package com.example.permissionanalzyer.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View

fun Context.getInstalledApplications() : List<ApplicationInfo>{
    return  packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
}

fun ApplicationInfo.getAppName(context: Context) : String {
    return context.packageManager.getApplicationLabel(this).toString()
}

fun ApplicationInfo.getAppIcon(context : Context) : Drawable{
    return context.packageManager.getApplicationIcon(this)
}

fun View.visible(){
    this.visibility = View.VISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}
fun Context.countDangerousPermissions(permissions: List<String>): Int {
    return permissions.count { permission ->
        try {
            val permissionInfo = this.packageManager.getPermissionInfo(permission, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // API 28 and above
                permissionInfo.protection == PermissionInfo.PROTECTION_DANGEROUS
            } else {
                // Below API 28
                permissionInfo.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE == PermissionInfo.PROTECTION_DANGEROUS
            }
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
