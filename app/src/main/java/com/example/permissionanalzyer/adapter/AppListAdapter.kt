package com.example.permissionanalzyer.adapter

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.permissionanalzyer.databinding.AppListCardBinding

class AppListAdapter(private val itemOnClick: (ApplicationInfo) -> Unit) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    private lateinit var binding: AppListCardBinding
    private var appList: MutableList<ApplicationInfo> = mutableListOf()

    inner class AppListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAppIcon : ImageView = binding.ivAppIcon
        val tvAppName: TextView = binding.tvPermissionName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = AppListCardBinding.inflate(inflater, parent, false)
        return AppListViewHolder(binding.root)
    }

    override fun getItemCount() = appList.size

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val context = holder.itemView.context
        val appName = context.packageManager.getApplicationLabel(appList[position]).toString()
        holder.ivAppIcon.setImageDrawable(context.packageManager.getApplicationIcon(appList[position]))
        holder.tvAppName.text = appName
        holder.itemView.setOnClickListener {
            itemOnClick.invoke(appList[position])
        }
    }

    fun submitList(appListParam: List<ApplicationInfo>) {
        appList.addAll(appListParam)
        notifyDataSetChanged()
    }

}