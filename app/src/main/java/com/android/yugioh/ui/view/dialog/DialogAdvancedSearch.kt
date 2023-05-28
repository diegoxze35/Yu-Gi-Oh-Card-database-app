package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.yugioh.R
import com.android.yugioh.databinding.DialogAdvancedSearchBinding
import com.android.yugioh.di.FragmentModule
import com.android.yugioh.domain.SearchCardByOptionsOlineUseCase
import com.android.yugioh.ui.viewmodel.CardViewModel

class DialogAdvancedSearch(
	private val archetypes: List<String>,
	private val adapters: Map<String, ArrayAdapter<*>>,
	private val advancedSearchUseCase: SearchCardByOptionsOlineUseCase
) : DialogFragment(R.layout.dialog_advanced_search) {
	private val activity: AppCompatActivity by lazy { requireActivity() as AppCompatActivity }
	private var _dialogBinding: DialogAdvancedSearchBinding? = null
	private val dialogBinding: DialogAdvancedSearchBinding get() = _dialogBinding!!
	private val options = StringBuilder()
	private val viewModel: CardViewModel by activityViewModels()

	companion object {
		private const val TYPE = "type"
		private const val CARD = "card"
		const val SPLIT = ','
		const val EQ = '='
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity.supportActionBar?.hide()
		isCancelable = false
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_dialogBinding = DialogAdvancedSearchBinding.inflate(inflater, container, false).also {
			it.autoCompleteTextViewTypes.setAdapter(
				adapters.getValue(FragmentModule.MONSTER_TYPE_VALUES)
			)
			it.autoCompleteTextViewAttribute.setAdapter(
				adapters.getValue(FragmentModule.ATTRIBUTE_MONSTER_VALUES)
			)
			it.autoCompleteTextViewLevelMonster.setAdapter(
				adapters.getValue(FragmentModule.LEVEL_MONSTERS_SEQUENCE)
			)
			it.autoCompleteTextViewAtkMonster.setAdapter(
				adapters.getValue(FragmentModule.ATK_AND_DEFENSE_MONSTERS_SEQUENCE)
			)
			it.autoCompleteTextViewDefMonster.setAdapter(
				adapters.getValue(FragmentModule.ATK_AND_DEFENSE_MONSTERS_SEQUENCE)
			)
			it.autoCompleteTextViewScaleMonster.setAdapter(
				adapters.getValue(FragmentModule.SCALE_PENDUMULUM_SEQUENCE)
			)
			it.autoCompleteTextArchetype.setAdapter(
				ArrayAdapter(requireContext(), R.layout.item_auto_complete_text_view, archetypes)
			)
			it.autoCompleteTextFormatCard.setAdapter(
				adapters.getValue(FragmentModule.CARD_FORMATS)
			)
		}
		return dialogBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		dialogBinding.apply {
			imageButtonClose.setOnClickListener { activity.supportActionBar?.show(); dismiss() }
			radioGroup.setOnCheckedChangeListener { _, radioButtonId ->
				options.clear()
				buttonSubmit.isEnabled = true
				textInputLayoutRace.isGone = false
				textInputLayoutFormatCard.isGone = false
				textInputLayoutArchetype.isGone = false
				autoCompleteTextViewRaces.text.clear()
				autoCompleteTextViewRaces.setAdapter(
					when (radioButtonId) {
						radioButtonMonster.id -> adapters.getValue(FragmentModule.RACE_MONSTER_CARD_VALUES)
						radioButtonSpell.id -> {
							options.append("${TYPE}${EQ}spell ${CARD}$SPLIT")
							adapters.getValue(FragmentModule.RACE_SPELL_CARD_VALUES)
						}

						radioButtonTrap.id -> {
							options.append("${TYPE}${EQ}trap ${CARD}$SPLIT")
							adapters.getValue(FragmentModule.RACE_TRAP_CARD_VALUES)
						}

						else -> {
							options.append("${TYPE}${EQ}skill ${CARD}$SPLIT")
							adapters.getValue(FragmentModule.RACE_SKILL_CARD_VALUES)
						}
					}
				)
				updateDialog(
					goneOptions = (radioButtonId != radioButtonMonster.id),
					goneFormatOptions = (radioButtonId == radioButtonSkill.id)
				)
			}
			buttonSubmit.setOnClickListener {
				when (radioGroup.checkedRadioButtonId) {
					radioButtonMonster.id -> listOf(
						autoCompleteTextViewTypes,
						autoCompleteTextViewRaces,
						autoCompleteTextViewAttribute,
						autoCompleteTextFormatCard,
						autoCompleteTextArchetype,
						autoCompleteTextViewLevelMonster,
						autoCompleteTextViewAtkMonster,
						autoCompleteTextViewDefMonster,
						autoCompleteTextViewScaleMonster
					)

					radioButtonSpell.id, radioButtonTrap.id -> listOf(
						autoCompleteTextViewRaces,
						autoCompleteTextFormatCard,
						autoCompleteTextArchetype
					)

					else -> listOf(
						autoCompleteTextViewRaces, autoCompleteTextArchetype
					)
				}.forEach {
					if (it.text.isNotEmpty() && it.text.toString() != FragmentModule.FIRST_ELEMENT_DROP_DOWN_MENU) options.append(
						"${it.hint}${EQ}${it.text}$SPLIT"
					)
				}

				viewModel.querySearch = "$options".dropLast(1) //delete last ','
				viewModel.setSearchUseCase(advancedSearchUseCase)
				activity.supportActionBar?.show()
				findNavController().navigate(R.id.action_dialogAdvancedSearch_to_listCardFragment)
			}
		}
	}

	private fun updateDialog(goneOptions: Boolean, goneFormatOptions: Boolean) {
		dialogBinding.apply {
			textInputLayoutType.isGone = goneOptions
			textInputLayoutAttribute.isGone = goneOptions
			textInputLayoutLevelMonster.isGone = goneOptions
			textInputLayoutAtkMonster.isGone = goneOptions
			textInputLayoutDefMonster.isGone = goneOptions
			textInputLayoutScaleMonster.isGone = goneOptions
			textInputLayoutFormatCard.isGone = goneFormatOptions
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_dialogBinding = null
	}
}