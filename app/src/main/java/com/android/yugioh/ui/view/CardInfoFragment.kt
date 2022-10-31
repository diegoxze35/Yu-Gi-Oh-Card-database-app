package com.android.yugioh.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.android.yugioh.R
import com.android.yugioh.model.data.Card
import com.android.yugioh.model.data.MonsterCard
import com.android.yugioh.model.data.SkillCard
import com.android.yugioh.model.data.SpellTrapCard
import com.android.yugioh.model.data.MonsterCard.Companion.MonsterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import com.android.yugioh.model.data.Enum
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class CardInfoFragment : Fragment() {
	
	private val viewModel: CardViewModel by activityViewModels()
	private lateinit var card: Card
	private lateinit var atkText: TextView
	private lateinit var defText: TextView
	private lateinit var levelText: TextView
	private lateinit var imageCard: ImageView
	private lateinit var textAttribute: TextView
	private lateinit var textBanOCG: TextView
	private lateinit var textBanTCG: TextView
	private lateinit var textScalePendulum: TextView
	private lateinit var textArchetype: TextView
	private lateinit var layout: ConstraintLayout
	private lateinit var bitmap: Deferred<Bitmap>
	
	companion object {
		private const val START_SCROLL = 0
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_card_info, container, false)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		card = viewModel.currentCard.value!!
		bitmap = CoroutineScope(Dispatchers.IO).async {
			URL(card.cardImages[0].imageUrl).getBitmap()
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		view.findViewById<FloatingActionButton>(R.id.button_add_to_deck).setOnClickListener {
			Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
		}
		(requireActivity() as MainCardActivity).updateToolbar(this)
		view.findViewById<ScrollView>(R.id.main_scroll).apply {
			post {
				scrollTo(START_SCROLL, START_SCROLL)
			}
		}
		view.findViewById<View>(R.id.layout_info_card).setBackgroundColor(
			ContextCompat.getColor(requireContext(), card.type.color)
		)
		atkText = view.findViewById(R.id.text_view_atk)
		defText = view.findViewById(R.id.text_view_def)
		levelText = view.findViewById(R.id.text_view_level_monster)
		imageCard = view.findViewById(R.id.image_view_full_card)
		textAttribute = view.findViewById(R.id.text_view_attribute)
		textArchetype = view.findViewById(R.id.text_view_archetype)
		textBanOCG = view.findViewById(R.id.text_view_ban_list_OCG)
		textBanTCG = view.findViewById(R.id.textViewBanListTCG)
		textScalePendulum = view.findViewById(R.id.text_view_scale)
		view.findViewById<TextView>(R.id.text_view_id).text = "$id"
		view.findViewById<TextView>(R.id.textViewDescription).text = card.description
		
		card.archetype?.let {
			textArchetype.text = it
		} ?: kotlin.run {
			textArchetype.isGone = true
			if (card is SpellTrapCard) {
				(ConstraintSet()).run {
					layout = view.findViewById(R.id.layout_data_card)
					clone(layout)
					val idGuideline = R.id.guidelineDivider1
					connect(textBanOCG.id, ConstraintSet.TOP, idGuideline, ConstraintSet.BOTTOM)
					connect(textBanTCG.id, ConstraintSet.TOP, idGuideline, ConstraintSet.BOTTOM)
					applyTo(layout)
				}
			}
		}
		
		view.findViewById<TextView>(R.id.text_view_type)
			.setIconAndText(R.string.any_text, card.type)
		view.findViewById<TextView>(R.id.text_view_race)
			.setIconAndText(R.string.any_text, card.race)
		card.format?.let {
			it.banTCG?.let { TCG -> textBanTCG.setIconAndText(R.string.tcg_ban_list, TCG) }
				?: textBanTCG.apply {
					isGone = true
				}
			it.banOCG?.let { OCG -> textBanOCG.setIconAndText(R.string.ocg_ban_list, OCG) }
				?: textBanOCG.apply {
					isGone = true
				}
		} ?: kotlin.run {
			textBanOCG.isGone = true
			textBanTCG.isGone = true
			(ConstraintSet()).run {
				layout = view.findViewById(R.id.layout_data_card)
				clone(layout)
				connect(
					R.id.textViewDescription,
					ConstraintSet.TOP,
					if (card is SkillCard)
						R.id.guidelineDivider1
					else
						R.id.guidelineDivider2,
					ConstraintSet.BOTTOM
				)
				applyTo(layout)
			}
		}
		
		if (card is MonsterCard) {
			val monsterCard = card as MonsterCard
			atkText.text = "${monsterCard.attack}"
			monsterCard.defense?.let { defText.text = "$it" } ?: defText.apply { isGone = true }
			levelText.apply {
				lateinit var text: String
				setCompoundDrawablesWithIntrinsicBounds(
					0, 0, 0, when (monsterCard.type) {
						MonsterType.XYZ_MONSTER,
						MonsterType.XYZ_PENDULUM_EFFECT_MONSTER -> {
							text = getString(R.string.level_info, monsterCard.level)
							R.drawable.level_xyz_monster_s
						}
						MonsterType.LINK_MONSTER -> {
							text = getString(R.string.linkval_info, monsterCard.level)
							R.drawable.linkval_s
						}
						else -> {
							text = getString(R.string.level_info, monsterCard.level)
							R.drawable.level_monster_s
						}
					}
				)
				this.text = text
			}
			view.findViewById<TextView>(R.id.text_view_attribute)
				.setIconAndText(R.string.any_text, monsterCard.attribute)
			monsterCard.scaleOfPendulum?.let {
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
		
		MainScope().launch {
			imageCard.setImageBitmap(bitmap.await())
		}
	}
	
	private fun TextView.setIconAndText(stringResource: Int, value: Enum) {
		this.apply {
			setCompoundDrawablesWithIntrinsicBounds(
				0, 0, 0, value.icon
			)
			text = getString(
				stringResource,
				"$value"
			)
		}
	}
	
	private fun URL.getBitmap(): Bitmap = BitmapFactory.decodeStream(openStream())
	
}