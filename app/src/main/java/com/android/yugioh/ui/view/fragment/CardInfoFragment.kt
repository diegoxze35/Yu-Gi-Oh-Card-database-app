package com.android.yugioh.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.android.yugioh.R
import com.android.yugioh.databinding.FragmentCardInfoBinding
import com.android.yugioh.domain.data.MonsterCard
import com.android.yugioh.domain.data.SkillCard
import com.android.yugioh.domain.data.SpellTrapCard
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.DomainEnum
import com.android.yugioh.ui.view.MainCardActivity
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.squareup.picasso.Picasso

class CardInfoFragment(private val picasso: Picasso) : Fragment() {

	private val viewModel: CardViewModel by activityViewModels()
	private var _infoBinding: FragmentCardInfoBinding? = null
	private val infoBinding: FragmentCardInfoBinding get() = _infoBinding!!

	companion object {
		private const val START_SCROLL = 0
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		with(requireActivity() as MainCardActivity) {
			supportActionBar?.title = viewModel.clickedCard.name
			mainBinding.searchView.isGone = true
			mainBinding.iconToolbar.isGone = true
		}
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_infoBinding = FragmentCardInfoBinding.inflate(layoutInflater, container, false)
		with(infoBinding.imageViewFullCard) {
			/*Using Picasso cache*/
			picasso.load(viewModel.clickedCard.cardImages[0].imageUrl).noFade().into(this)
		}
		return infoBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		infoBinding.apply {
			buttonAddToDeck.setOnClickListener {
				//Add to deck functionality to be implemented/
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
			textViewType.setIconAndText(
				R.string.any_text,
				viewModel.clickedCard.type
			)
			textViewRace.setIconAndText(
				R.string.any_text,
				viewModel.clickedCard.race
			)
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
				with(viewModel.clickedCard as MonsterCard) {
					textViewAtk.text = "$attack"
					defense?.let { textViewDef.text = "$it" } ?: textViewDef.apply { isGone = true }
					textViewLevelMonster.apply {
						val text: String
						setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, when (type) {
								MonsterType.XYZ_MONSTER,
								MonsterType.XYZ_PENDULUM_EFFECT_MONSTER -> {
									text = getString(R.string.level_info, level)
									R.drawable.level_xyz_monster_s
								}

								MonsterType.LINK_MONSTER -> {
									text = getString(R.string.linkval_info, level)
									R.drawable.linkval_s
								}

								else -> {
									text = getString(R.string.level_info, level)
									R.drawable.level_monster_s
								}
							}
						)
						this.text = text
					}
					attribute?.let {
						textViewAttribute.setIconAndText(
							R.string.any_text,
							it
						)
					}
					scaleOfPendulum?.let {
						textViewScale.text = getString(R.string.pendulum_scale, it)
					}
						?: textViewScale.apply {
							isGone = true
						}
				}

			} else {
				textViewAtk.isGone = true
				textViewDef.isGone = true
				textViewLevelMonster.isGone = true
				textViewAttribute.isGone = true
				textViewScale.isGone = true
			}

			imageViewFullCard.apply {
				if (drawable != null) {
					setOnClickListener {
						findNavController().navigate(
							R.id.action_cardInfoFragment_to_imageCardFragment,
							args = null,
							navOptions = null,
							navigatorExtras = FragmentNavigatorExtras(this to viewModel.clickedCard.name)
						)
					}
					transitionName = viewModel.clickedCard.name
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