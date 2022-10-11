package com.android.yugioh.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isGone
import com.android.yugioh.R
import com.android.yugioh.model.data.Card
import com.android.yugioh.model.data.Enum
import com.android.yugioh.model.data.MonsterCard
import com.android.yugioh.model.data.MonsterCard.CREATOR.MonsterType
import com.android.yugioh.ui.view.CardAdapter.Companion.BACKGROUND_COLOR
import com.android.yugioh.ui.view.CardAdapter.Companion.CARD_PARCELABLE
import com.android.yugioh.ui.view.CardAdapter.Companion.TEXT_COLOR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class CardInfoActivity : AppCompatActivity() {
	
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
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_card_info)
		
		atkText = findViewById(R.id.textViewAtk)
		defText = findViewById(R.id.textViewDef)
		levelText = findViewById(R.id.textViewLevelMonster)
		
		imageCard = findViewById(R.id.imageViewFullCard)
		cardName = findViewById(R.id.textViewNameCard)
		textAttribute = findViewById(R.id.textViewAttribute)
		textArchetype = findViewById(R.id.textViewArchetype)
		
		textBanOCG = findViewById(R.id.textViewBanListOCG)
		textBanTCG = findViewById(R.id.textViewBanListTCG)
		textScalePendulum = findViewById(R.id.textViewScale)
		
		lateinit var bitmap: Deferred<Bitmap>
		
		with((intent.extras!!).run {
			findViewById<ScrollView>(R.id.mainScroll).setBackgroundColor(getInt(BACKGROUND_COLOR))
			cardName.setTextColor(getInt(TEXT_COLOR))
			(getParcelable<Card>(CARD_PARCELABLE)!!.also {
				val url = URL(it.card_images[0].imageUrl)
				bitmap = CoroutineScope(Dispatchers.IO).async {
					getBitmapFromURL(url)
				}
			})
		}) {
			cardName.text = name
			findViewById<TextView>(R.id.textViewId).text = "$id"
			findViewById<TextView>(R.id.textViewDescription).text = description
			
			archetype?.let {
				textArchetype.text = it
			} ?: kotlin.run {
				textArchetype.isGone = true
				if (this@with !is MonsterCard) {
					(ConstraintSet()).run {
						layout = findViewById(R.id.constraintLayoutInfoCard)
						clone(layout)
						val idGuideline = R.id.guidelineDivider1
						connect(textBanOCG.id, ConstraintSet.TOP, idGuideline, ConstraintSet.BOTTOM)
						connect(textBanTCG.id, ConstraintSet.TOP, idGuideline, ConstraintSet.BOTTOM)
						applyTo(layout)
					}
				}
			}
			
			findViewById<TextView>(R.id.textViewType).setIconAndText(type)
			findViewById<TextView>(R.id.textViewRace).setIconAndText(race)
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
					layout = findViewById(R.id.constraintLayoutInfoCard)
					clone(layout)
					connect(
						R.id.textViewDescription,
						ConstraintSet.TOP,
						R.id.guidelineDivider1,
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
				findViewById<TextView>(R.id.textViewAttribute).setIconAndText(attribute)
				
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
	
	private fun getBitmapFromURL(url: URL): Bitmap = BitmapFactory.decodeStream(url.openStream())
	
}