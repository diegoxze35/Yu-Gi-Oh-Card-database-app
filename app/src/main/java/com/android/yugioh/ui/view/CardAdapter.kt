package com.android.yugioh.ui.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.instances.Picasso
import com.android.yugioh.instances.Picasso.setImageFromUrlInImageView
import com.android.yugioh.model.data.Card

class CardAdapter(private val cards: MutableList<Card>, private val onCLick: (Card) -> Unit) :
	RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
	
	private val picasso = Picasso()
	
	fun addListCard(cards: List<Card>) {
		val position = itemCount
		this.cards.run {
			addAll(cards.filter {
				!contains(it)
			})
		}
		notifyItemRangeInserted(position, cards.size)
	}
	
	fun addFilterList(filterCards: List<Card>) {
		cards.run {
			val lastItemCount = itemCount
			clear()
			notifyItemRangeRemoved(0, lastItemCount)
			val positionStart = itemCount
			cards.addAll(filterCards)
			notifyItemRangeInserted(positionStart, itemCount)
		}
	}
	
	class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		
		val contextActivity: Context
		val cardView: CardView
		val imageCard: ImageView
		val cardName: TextView
		val typeCard: TextView
		val raceCard: TextView
		
		init {
			with(view) {
				contextActivity = context
				cardView = findViewById(R.id.cardViewItemCard)
				imageCard = findViewById(R.id.imageViewCard)
				cardName = findViewById(R.id.textViewCardName)
				typeCard = findViewById(R.id.textViewTypeCard)
				raceCard = findViewById(R.id.textViewRaceCard)
			}
		}
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
		CardViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.layout_item_card, parent, false)
		)
	
	override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
		holder.apply {
			cards[position].let { card ->
				cardName.text = card.name
				typeCard.text = card.type.toString()
				raceCard.text = card.race.toString()
				picasso.setImageFromUrlInImageView(
					card.card_images[0].imageUrlSmall,
					imageCard
				)
				var currentColor: Int = Color.WHITE
				cardView.setCardBackgroundColor(
					ContextCompat.getColor(
						contextActivity, (card.type.color).also {
							if (it == R.color.colorSynchronyMonster) currentColor = Color.BLACK
						}
					)
				)
				cardName.setTextColor(currentColor)
				typeCard.setTextColor(currentColor)
				raceCard.setTextColor(currentColor)
				itemView.setOnClickListener { onCLick(card) }
			}
		}
	}
	
	override fun getItemCount(): Int = cards.size
	
}