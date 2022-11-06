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
import com.android.yugioh.model.data.Enum
import com.android.yugioh.model.data.MonsterCard.Companion.RaceMonsterCard
import com.android.yugioh.model.data.SpellTrapCard.Companion.RaceSpellTrap
import com.android.yugioh.model.data.SkillCard.Companion.RaceSkill

class DialogAdvancedSearch(private val adapters: Array<ArrayAdapter<*>>) :
	DialogFragment(R.layout.dialog_advanced_search) {
	
	private var _dialogBinding: DialogAdvancedSearchBinding? = null
	private val dialogBinding: DialogAdvancedSearchBinding get() = _dialogBinding!!
	
	private val map by lazy {
		with(dialogBinding) {
			mapOf(
				textInputLayoutType to autoCompleteTextViewTypes,
				textInputLayoutRace to autoCompleteTextViewRaces,
				textInputLayoutAttribute to autoCompleteTextViewAttribute,
				textInputLayoutLevelMonster to autoCompleteTextViewLevelMonster,
				textInputLayoutAtkMonster to autoCompleteTextViewAtkMonster,
				textInputLayoutDefMonster to autoCompleteTextViewDefMonster,
				textInputLayoutScaleMonster to autoCompleteTextViewScaleMonster
			)
		}
	}
	
	private fun ArrayAdapter<Enum>.updateItems(newItems: List<Enum>) {
		clear()
		addAll(newItems)
		notifyDataSetChanged()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {
		_dialogBinding = DialogAdvancedSearchBinding.inflate(inflater, container, false)
		return dialogBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		isCancelable = false
		dialogBinding.apply {
			imageButtonClose.setOnClickListener {
				dismiss()
			}
			radioGroup.setOnCheckedChangeListener { _, radioButtonId ->
				@Suppress("UNCHECKED_CAST")
				applyChanges(
					when (radioButtonId) {
						radioButtonMonster.id -> {
							(adapters[1] as ArrayAdapter<Enum>).updateItems(
								RaceMonsterCard.values().toMutableList()
							)
							Array(map.size) { false }
						}
						radioButtonSpell.id -> {
							(adapters[1] as ArrayAdapter<Enum>).updateItems(
								RaceSpellTrap.values().toMutableList().also {
									it.remove(RaceSpellTrap.COUNTER)
								}
							)
							arrayOf(true, false, true, true, true, true, true)
						}
						radioButtonTrap.id -> {
							(adapters[1] as ArrayAdapter<Enum>).updateItems(
								mutableListOf(
									RaceSpellTrap.NORMAL,
									RaceSpellTrap.CONTINUOUS,
									RaceSpellTrap.COUNTER
								)
							)
							arrayOf(true, false, true, true, true, true, true)
						}
						else -> {
							(adapters[1] as ArrayAdapter<Enum>).updateItems(
								RaceSkill.values().toMutableList()
							)
							arrayOf(true, false, true, true, true, true, true)
						}
					}
				)
			}
		}
	}
	
	private fun applyChanges(theyGone: Array<Boolean>) {
		for ((index, textInputLayout) in map.keys.withIndex()) {
			textInputLayout.isGone = theyGone[index]
			map[textInputLayout]!!.apply {
				text.clear()
				if (adapter == null) setAdapter(adapters[index])
			}
		}
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_dialogBinding = null
	}
}