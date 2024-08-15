package com.example.permissionanalzyer.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

fun Context.getInstalledApplications() : List<ApplicationInfo>{
    return  packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
}

fun ApplicationInfo.getAppName(context: Context) : String {
    return context.packageManager.getApplicationLabel(this).toString()
}

fun ApplicationInfo.getAppIcon(context : Context) : Drawable{
    return context.packageManager.getApplicationIcon(this)
}