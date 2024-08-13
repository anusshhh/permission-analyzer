package com.example.permissionanalzyer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.permissionanalzyer.databinding.PermissionCardBinding

class AppInfoAdapter : RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>() {

    private lateinit var binding: PermissionCardBinding
    private var appInfoList: MutableList<String> = mutableListOf()

    inner class AppInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = binding.tvPermissionName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = PermissionCardBinding.inflate(inflater, parent, false)
        return AppInfoViewHolder(binding.root)
    }

    override fun getItemCount() = appInfoList.size

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        holder.textView.text = appInfoList[position]
    }

    fun submitList(appInfoListParam: List<String>) {
        appInfoList.addAll(appInfoListParam)
    }

}