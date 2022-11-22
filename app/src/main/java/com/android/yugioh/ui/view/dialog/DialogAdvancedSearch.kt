package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.yugioh.R
import com.android.yugioh.databinding.DialogAdvancedSearchBinding
import com.android.yugioh.ui.viewmodel.CardViewModel

class DialogAdvancedSearch(private val adapters: Array<Array<ArrayAdapter<*>>>) :
	DialogFragment(R.layout.dialog_advanced_search) {
	
	companion object {
		const val INDEX_FIRST_ADAPTERS = 0
		const val INDEX_SECOND_ADAPTERS = 1
		const val INDEX_THREE_ADAPTERS = 2
	}
	
	private val viewModel: CardViewModel by activityViewModels()
	
	private var _dialogBinding: DialogAdvancedSearchBinding? = null
	private val dialogBinding: DialogAdvancedSearchBinding get() = _dialogBinding!!
	
	private val map by lazy {
		with(dialogBinding) {
			mapOf(
				textInputLayoutType to autoCompleteTextViewTypes,
				textInputLayoutAttribute to autoCompleteTextViewAttribute,
				textInputLayoutLevelMonster to autoCompleteTextViewLevelMonster,
				textInputLayoutAtkMonster to autoCompleteTextViewAtkMonster,
				textInputLayoutDefMonster to autoCompleteTextViewDefMonster,
				textInputLayoutScaleMonster to autoCompleteTextViewScaleMonster
			)
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		isCancelable = false
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_dialogBinding = DialogAdvancedSearchBinding.inflate(inflater, container, false)
		return dialogBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		for ((index, autoCompleteTextView) in map.values.withIndex()) {
			autoCompleteTextView.setAdapter(adapters[INDEX_FIRST_ADAPTERS][index])
		}
		dialogBinding.apply {
			if (viewModel.archetypesIsReady) {
				viewModel.archetypes.also {
					autoCompleteTextArchetype.setAdapter(
						ArrayAdapter(
							requireContext(),
							R.layout.item_auto_complete_text_view,
							it
						)
					)
				}
			}
			buttonSubmit.setOnClickListener {
				/*TODO()*/
			}
			autoCompleteTextFormatCard.setAdapter(adapters[INDEX_THREE_ADAPTERS][INDEX_FIRST_ADAPTERS])
			val getIndex = mapOf(
				radioButtonMonster.id to 0,
				radioButtonSpell.id to 1,
				radioButtonTrap.id to 2,
				radioButtonSkill.id to 3
			)
			imageButtonClose.setOnClickListener { dismiss() }
			radioGroup.setOnCheckedChangeListener { _, radioButtonId ->
				textInputLayoutRace.isGone = false
				textInputLayoutFormatCard.isGone = false
				textInputLayoutArchetype.isGone = false
				autoCompleteTextViewRaces.text.clear()
				applyChanges(
					(radioButtonId != radioButtonMonster.id), getIndex[radioButtonId]!!
				)
				buttonSubmit.isEnabled = true
			}
		}
	}
	
	private fun applyChanges(isGone: Boolean, indexRaceAdapter: Int) {
		for (textInputLayout in map.keys) {
			textInputLayout.isGone = isGone
		}
		dialogBinding.autoCompleteTextViewRaces.setAdapter(
			adapters[INDEX_SECOND_ADAPTERS][indexRaceAdapter]
		)
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_dialogBinding = null
	}
}