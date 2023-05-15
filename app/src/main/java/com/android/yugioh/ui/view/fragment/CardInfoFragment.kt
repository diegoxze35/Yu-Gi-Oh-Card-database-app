package com.android.yugioh.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.yugioh.R
import com.android.yugioh.databinding.FragmentCardInfoBinding
import com.android.yugioh.domain.data.MonsterCard
import com.android.yugioh.domain.data.SkillCard
import com.android.yugioh.domain.data.SpellTrapCard
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.DomainEnum
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.squareup.picasso.Picasso

class CardInfoFragment(private val picasso: Picasso) : Fragment() {

	private val viewModel: CardViewModel by activityViewModels()
	private var _infoBinding: FragmentCardInfoBinding? = null
	private val infoBinding: FragmentCardInfoBinding get() = _infoBinding!!

	companion object {
		private const val START_SCROLL = 0
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_infoBinding = FragmentCardInfoBinding.inflate(layoutInflater, container, false)
		with(infoBinding.imageViewFullCard) {
			/*Using Picasso cache*/
			picasso.load(viewModel.clickedCard.cardImages[0].imageUrl).noFade().into(this)
			startAnimation(
				AnimationUtils.loadAnimation(context, R.anim.scale_enter_anim)
			)
		}
		return infoBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		infoBinding.apply {
			buttonAddToDeck.setOnClickListener {
				Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
			}
			mainScroll.apply {
				post {
					scrollTo(START_SCROLL, START_SCROLL)
				}
			}
			root.setBackgroundColor(
				ContextCompat.getColor(
					requireContext(),
					viewModel.clickedCard.type.color
				)
			)
			textViewId.text = "${viewModel.clickedCard.id}"
			textViewDescription.text = viewModel.clickedCard.description
			viewModel.clickedCard.archetype?.let {
				textViewArchetype.text = it
			} ?: kotlin.run {
				textViewArchetype.isGone = true
				if (viewModel.clickedCard is SpellTrapCard) {
					(ConstraintSet()).run {
						clone(layoutDataCard)
						connect(
							textViewBanListOCG.id,
							ConstraintSet.TOP,
							guidelineDivider1.id,
							ConstraintSet.BOTTOM
						)
						connect(
							textViewBanListTCG.id,
							ConstraintSet.TOP,
							guidelineDivider1.id,
							ConstraintSet.BOTTOM
						)
						applyTo(layoutDataCard)
					}
				}
			}
			textViewType.setIconAndText(R.string.any_text, viewModel.clickedCard.type)
			textViewRace.setIconAndText(R.string.any_text, viewModel.clickedCard.race)
			viewModel.clickedCard.format?.let {
				it.tcg?.banListState?.let { TCG ->
					textViewBanListTCG.setIconAndText(
						R.string.tcg_ban_list,
						TCG
					)
				}
					?: textViewBanListTCG.apply {
						isGone = true
					}
				it.ocg?.banListState?.let { OCG ->
					textViewBanListOCG.setIconAndText(
						R.string.ocg_ban_list,
						OCG
					)
				}
					?: textViewBanListOCG.apply {
						isGone = true
					}
			} ?: kotlin.run {
				textViewBanListOCG.isGone = true
				textViewBanListTCG.isGone = true
				(ConstraintSet()).run {
					clone(layoutDataCard)
					connect(
						textViewDescription.id,
						ConstraintSet.TOP,
						if (viewModel.clickedCard is SkillCard)
							guidelineDivider1.id
						else
							guidelineDivider2.id,
						ConstraintSet.BOTTOM
					)
					applyTo(layoutDataCard)
				}
			}
			if (viewModel.clickedCard is MonsterCard) {
				val monsterCard = viewModel.clickedCard as MonsterCard
				textViewAtk.text = "${monsterCard.attack}"
				monsterCard.defense?.let { textViewDef.text = "$it" }
					?: textViewDef.apply { isGone = true }
				textViewLevelMonster.apply {
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
				textViewAttribute.setIconAndText(R.string.any_text, monsterCard.attribute)
				monsterCard.scaleOfPendulum?.let {
					textViewScale.text = getString(R.string.pendulum_scale, it)
				} ?: textViewScale.apply {
					isGone = true
				}

			} else {
				textViewAtk.isGone = true
				textViewDef.isGone = true
				textViewLevelMonster.isGone = true
				textViewAttribute.isGone = true
				textViewScale.isGone = true
			}

			if(imageViewFullCard.drawable != null) {
				imageViewFullCard.setOnClickListener {
					findNavController().navigate(R.id.action_cardInfoFragment_to_dialogImageCard)
				}
			}
		}
	}

	private fun TextView.setIconAndText(@StringRes stringResource: Int, value: DomainEnum) {
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

	override fun onDestroyView() {
		super.onDestroyView()
		_infoBinding = null
	}

}