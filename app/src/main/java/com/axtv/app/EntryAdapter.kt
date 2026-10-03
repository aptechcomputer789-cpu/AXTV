package com.axtv.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axtv.app.databinding.ItemEntryBinding

class EntryAdapter(
    private val onClick: (RemoteEntry) -> Unit
) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    private val items = mutableListOf<RemoteEntry>()

    fun submitList(newItems: List<RemoteEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class EntryViewHolder(
        private val binding: ItemEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RemoteEntry) {
            binding.nameText.text = item.name
            binding.urlText.text = item.url
            binding.iconText.text = when (item.type) {
                EntryType.FOLDER -> "📁"
                EntryType.VIDEO -> "▶"
                EntryType.FILE -> "📄"
            }
            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnFocusChangeListener { view, hasFocus ->
                view.alpha = if (hasFocus) 0.82f else 1f
                view.scaleX = if (hasFocus) 1.02f else 1f
                view.scaleY = if (hasFocus) 1.02f else 1f
            }
        }
    }
}
