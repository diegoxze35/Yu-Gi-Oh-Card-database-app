package com.android.yugioh.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.databinding.LayoutItemDeckBinding
import com.android.yugioh.domain.data.DeckItem

class DeckMetadataAdapter(private val onClick: (DeckItem) -> Unit) : ListAdapter<DeckItem, DeckMetadataAdapter.DeckMetadataViewHolder>(
	DiffCallback
) {

	class DeckMetadataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val bindingItemDeck = LayoutItemDeckBinding.bind(view)
		val contextActivity: Context = view.context
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckMetadataViewHolder {
		return DeckMetadataViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.layout_item_deck, parent, false)
		)
	}

	override fun onBindViewHolder(holder: DeckMetadataViewHolder, position: Int) {
		val deckItem = getItem(position)
		holder.bindingItemDeck.apply {
			root.setOnClickListener {
				onClick(deckItem)
			}
			deckName.text = deckItem.name
			deckDate.text = deckItem.createdAt.toString()
			deckLegal.apply {
				if (deckItem.isLegal) {
					text = holder.contextActivity.getString(R.string.legal_deck)
					setTextColor(holder.contextActivity.getColor(R.color.color_ok))
				}
				else {
					text = holder.contextActivity.getString(R.string.illegal_deck)
					setTextColor(holder.contextActivity.getColor(R.color.color_not_ok))
				}
			}
		}
	}

	private object DiffCallback : DiffUtil.ItemCallback<DeckItem>() {
		override fun areItemsTheSame(oldItem: DeckItem, newItem: DeckItem): Boolean =
			(oldItem.id == newItem.id)

		override fun areContentsTheSame(oldItem: DeckItem, newItem: DeckItem): Boolean =
			(oldItem == newItem)
	}
}