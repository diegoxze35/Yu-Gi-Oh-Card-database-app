package com.android.yugioh.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.android.yugioh.R
import com.android.yugioh.model.data.Card
import com.android.yugioh.model.data.MonsterCard
import com.android.yugioh.model.data.SkillCard
import com.android.yugioh.model.data.SpellTrapCard
import com.android.yugioh.model.data.MonsterCard.CREATOR.MonsterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import com.android.yugioh.model.data.Enum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class CardInfoFragment(private val card: Card) : Fragment() {
	
	private lateinit var atkText: TextView
	private lateinit var defText: TextView
	private lateinit var levelText: TextView
	private lateinit var imageCard: ImageView
	private lateinit var cardName: TextView
	private lateinit var textAttribute: TextView
	private lateinit var textBanOCG: TextView
	private lateinit var textBanTCG: TextView
	private lateinit var textScalePendulum: TextView
	private lateinit var textArchetype: TextView
	
	private lateinit var layout: ConstraintLayout
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_card_info, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		
		super.onViewCreated(view, savedInstanceState)
		
		atkText = view.findViewById(R.id.textViewAtk)
		defText = view.findViewById(R.id.textViewDef)
		levelText = view.findViewById(R.id.textViewLevelMonster)
		
		imageCard = view.findViewById(R.id.imageViewFullCard)
		cardName = view.findViewById(R.id.textViewNameCard)
		textAttribute = view.findViewById(R.id.textViewAttribute)
		textArchetype = view.findViewById(R.id.textViewArchetype)
		
		textBanOCG = view.findViewById(R.id.textViewBanListOCG)
		textBanTCG = view.findViewById(R.id.textViewBanListTCG)
		textScalePendulum = view.findViewById(R.id.textViewScale)
		
		lateinit var bitmap: Deferred<Bitmap>
		
		with(card) {
			bitmap = CoroutineScope(Dispatchers.IO).async {
				URL(card_images[0].imageUrl).getBitmap()
			}
			view.findViewById<ScrollView>(R.id.mainScroll).setBackgroundColor(
				ContextCompat.getColor(requireContext(), type.color.also {
					view.findViewById<TextView>(R.id.textViewNameCard).apply {
						text = name
						setTextColor(
							if (it == R.color.colorSynchronyMonster)
								Color.BLACK
							else
								Color.WHITE
						)
					}
				})
			)
			view.findViewById<TextView>(R.id.textViewId).text = "$id"
			view.findViewById<TextView>(R.id.textViewDescription).text = description
			
			archetype?.let {
				textArchetype.text = it
			} ?: kotlin.run {
				textArchetype.isGone = true
				if (this@with is SpellTrapCard) {
					(ConstraintSet()).run {
						layout = view.findViewById(R.id.constraintLayoutInfoCard)
						clone(layout)
						val idGuideline = R.id.guidelineDivider1
						connect(textBanOCG.id, ConstraintSet.TOP, idGuideline, ConstraintSet.BOTTOM)
						connect(textBanTCG.id, ConstraintSet.TOP, idGuideline, ConstraintSet.BOTTOM)
						applyTo(layout)
					}
				}
			}
			
			view.findViewById<TextView>(R.id.textViewType).setIconAndText(type)
			view.findViewById<TextView>(R.id.textViewRace).setIconAndText(race)
			format?.let {
				it.banTCG?.let { TCG -> textBanTCG.setIconAndText(TCG) } ?: textBanTCG.apply {
					isGone = true
				}
				it.banOCG?.let { OCG -> textBanOCG.setIconAndText(OCG) } ?: textBanOCG.apply {
					isGone = true
				}
			} ?: kotlin.run {
				textBanOCG.isGone = true
				textBanTCG.isGone = true
				(ConstraintSet()).run {
					layout = view.findViewById(R.id.constraintLayoutInfoCard)
					clone(layout)
					connect(
						R.id.textViewDescription,
						ConstraintSet.TOP,
						if (this@with is SkillCard)
							R.id.guidelineDivider1
						else
							R.id.guidelineDivider2,
						ConstraintSet.BOTTOM
					)
					applyTo(layout)
				}
			}
			
			if (this is MonsterCard) {
				
				atkText.text = "$attack"
				defense?.let { defText.text = "$it" } ?: defText.apply { isGone = true }
				
				level.let {
					levelText.apply {
						lateinit var text: String
						setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, when (type) {
								MonsterType.XYZ_MONSTER,
								MonsterType.XYZ_PENDULUM_EFFECT_MONSTER -> {
									text = getString(R.string.level_info, it)
									R.drawable.level_xyz_monster_s
								}
								MonsterType.LINK_MONSTER -> {
									text = getString(R.string.linkval_info, it)
									R.drawable.linkval_s
								}
								else -> {
									text = getString(R.string.level_info, it)
									R.drawable.level_monster_s
								}
							}
						)
						this.text = text
					}
				}
				view.findViewById<TextView>(R.id.textViewAttribute).setIconAndText(attribute)
				
				scaleOfPendulum?.let {
					textScalePendulum.text = getString(R.string.pendulum_scale, it)
				} ?: textScalePendulum.apply {
					isGone = true
				}
				
			} else {
				atkText.isGone = true
				defText.isGone = true
				levelText.isGone = true
				textAttribute.isGone = true
				textScalePendulum.isGone = true
			}
		}
		CoroutineScope(Dispatchers.Main).launch {
			imageCard.setImageBitmap(bitmap.await())
		}
	}
	
	private fun TextView.setIconAndText(value: Enum) {
		this.apply {
			setCompoundDrawablesWithIntrinsicBounds(
				0, 0, 0, value.icon
			)
			val textToShow = if (text.isEmpty()) "$value" else "$text $value"
			text = textToShow
		}
	}
	
	private fun URL.getBitmap(): Bitmap = BitmapFactory.decodeStream(openStream())
}