package com.android.yugioh.ui.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.database.entities.DeckEntity
import com.android.yugioh.databinding.ItemDeckBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DeckAdapter : ListAdapter<DeckEntity, DeckAdapter.DeckViewHolder>(DeckDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    inner class DeckViewHolder(private val binding: ItemDeckBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(deck: DeckEntity) {
            binding.apply {
                tvDeckName.text = deck.name
                val fechaFormateada = dateFormat.format(deck.createdAt)
                tvCreatedAt.text = "$fechaFormateada"
            }
        }
    }

    class DeckDiffCallback : DiffUtil.ItemCallback<DeckEntity>() {
        override fun areItemsTheSame(oldItem: DeckEntity, newItem: DeckEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeckEntity, newItem: DeckEntity): Boolean {
            return oldItem == newItem
        }
    }
}