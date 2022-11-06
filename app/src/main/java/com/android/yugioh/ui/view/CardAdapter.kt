package com.android.yugioh.ui.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.databinding.LayoutItemCardBinding
import com.android.yugioh.instances.Picasso
import com.android.yugioh.instances.Picasso.setImageFromUrlInImageView
import com.android.yugioh.model.data.Card

class CardAdapter(private val onCLick: (Card) -> Unit) :
	ListAdapter<Card, CardAdapter.CardViewHolder>(DiffCallback) {
	
	private val picasso = Picasso()
	
	class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val bindingItemCard = LayoutItemCardBinding.bind(view)
		val contextActivity: Context = view.context
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
		CardViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.layout_item_card, parent, false)
		)
	
	override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
		holder.apply {
			getItem(position).also { card ->
				bindingItemCard.apply {
					textViewCardName.text = card.name
					textViewTypeCard.text = card.type.toString()
					textViewRaceCard.text = card.race.toString()
					picasso.setImageFromUrlInImageView(
						card.cardImages[0].imageUrlSmall,
						imageViewCard
					)
					var currentColor: Int = Color.WHITE
					cardViewItemCard.setCardBackgroundColor(
						ContextCompat.getColor(
							contextActivity, (card.type.color).also {
								if (it == R.color.color_synchrony_monster) currentColor =
									Color.BLACK
							}
						)
					)
					textViewCardName.setTextColor(currentColor)
					textViewTypeCard.setTextColor(currentColor)
					textViewRaceCard.setTextColor(currentColor)
					itemView.setOnClickListener { onCLick(card) }
				}
			}
		}
	}
	
	private object DiffCallback : DiffUtil.ItemCallback<Card>() {
		override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean =
			(oldItem.id == newItem.id)
		
		override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean =
			(oldItem == newItem)
	}
	
}