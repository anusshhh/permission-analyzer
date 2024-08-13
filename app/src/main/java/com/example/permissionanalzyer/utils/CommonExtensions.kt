package com.example.permissionanalzyer.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

fun Context.getInstalledApplications() : List<ApplicationInfo>{
    return  packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
}

fun Context.getAppName(appInfo : ApplicationInfo) : String {
    return packageManager.getApplicationLabel(appInfo).toString()
}