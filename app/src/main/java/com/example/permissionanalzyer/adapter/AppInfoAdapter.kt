package com.example.permissionanalzyer.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.permissionanalzyer.R
import com.example.permissionanalzyer.databinding.PermissionCardBinding

class AppInfoAdapter(val isListPermission: Boolean = false) : RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>() {

    private lateinit var binding: PermissionCardBinding
    private var appInfoList: MutableList<String> = mutableListOf()
    lateinit var context : Context

    inner class AppInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = binding.tvPermissionName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = PermissionCardBinding.inflate(inflater, parent, false)
        context = parent.context
        return AppInfoViewHolder(binding.root)
    }

    override fun getItemCount() = appInfoList.size

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        holder.textView.text = appInfoList[position]
        if(isListPermission){
            holder.textView.setTextColor(getTextColorForPermissions(appInfoList[position]))
        }
    }

    fun submitList(appInfoListParam: List<String>) {
        appInfoList.addAll(appInfoListParam)
    }
    private fun getTextColorForPermissions(permission: String): Int {
        return try {
            val permissionInfo = context.packageManager.getPermissionInfo(permission, 0)
            val colorResId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val protection = permissionInfo.protection
                when (protection) {
                    PermissionInfo.PROTECTION_DANGEROUS -> R.color.red
                    PermissionInfo.PROTECTION_NORMAL -> R.color.secondary_blue
                    else -> R.color.secondary_blue
                }
            } else {
                val protectionLevel = permissionInfo.protectionLevel
                when (protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) {
                    PermissionInfo.PROTECTION_DANGEROUS -> R.color.red
                    PermissionInfo.PROTECTION_NORMAL -> R.color.secondary_blue
                    else -> R.color.secondary_blue
                }
            }
            ContextCompat.getColor(context, colorResId)
        } catch (e: PackageManager.NameNotFoundException) {
            ContextCompat.getColor(context, R.color.secondary_blue)
        }
    }

}