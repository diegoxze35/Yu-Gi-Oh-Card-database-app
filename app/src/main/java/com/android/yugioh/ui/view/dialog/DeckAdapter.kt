package com.android.yugioh.ui.view.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.database.entities.DeckEntity
import com.android.yugioh.databinding.ItemDeckBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DeckAdapter(private val onClick: (DeckEntity) -> Unit) :
	ListAdapter<DeckEntity, DeckAdapter.DeckViewHolder>(DeckDiffCallback()) {

	private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
		return DeckViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.item_deck, parent, false)
		)
	}

	override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
		val item = getItem(position)
		holder.binding.apply {
			root.setOnClickListener { onClick(item) }
			tvDeckName.text = item.name
			val date = dateFormat.format(item.createdAt)
			tvCreatedAt.text = "$date"
		}
	}

	class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val binding = ItemDeckBinding.bind(view)
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