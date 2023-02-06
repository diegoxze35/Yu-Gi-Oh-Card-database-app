package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.android.yugioh.R
import com.android.yugioh.databinding.DialogAdvancedSearchBinding
import com.android.yugioh.di.FragmentModule

class DialogAdvancedSearch(
	private val archetypes: List<String>, private val adapters: Map<String, ArrayAdapter<*>>
) : DialogFragment(R.layout.dialog_advanced_search) {

	private var _dialogBinding: DialogAdvancedSearchBinding? = null
	private val dialogBinding: DialogAdvancedSearchBinding get() = _dialogBinding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
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
			imageButtonClose.setOnClickListener { dismiss() }
			radioGroup.setOnCheckedChangeListener { _, radioButtonId ->
				buttonSubmit.isEnabled = true
				textInputLayoutRace.isGone = false
				textInputLayoutFormatCard.isGone = false
				textInputLayoutArchetype.isGone = false
				autoCompleteTextViewRaces.text.clear()
				autoCompleteTextViewRaces.setAdapter(
					when (radioButtonId) {
						radioButtonMonster.id -> adapters.getValue(FragmentModule.RACE_MONSTER_CARD_VALUES)
						radioButtonSpell.id -> adapters.getValue(FragmentModule.RACE_SPELL_CARD_VALUES)
						radioButtonTrap.id -> adapters.getValue(FragmentModule.RACE_TRAP_CARD_VALUES)
						else -> adapters.getValue(FragmentModule.RACE_SKILL_CARD_VALUES)
					}
				)
				updateDialog(gone = (radioButtonId != radioButtonMonster.id))
			}
			buttonSubmit.setOnClickListener {
				/*TODO()*/
			}
		}
	}

	private fun updateDialog(gone: Boolean) {
		dialogBinding.apply {
			textInputLayoutType.isGone = gone
			textInputLayoutAttribute.isGone = gone
			textInputLayoutLevelMonster.isGone = gone
			textInputLayoutAtkMonster.isGone = gone
			textInputLayoutDefMonster.isGone = gone
			textInputLayoutScaleMonster.isGone = gone
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_dialogBinding = null
	}
}