package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.yugioh.R
import com.android.yugioh.databinding.FragmentDialogAddToDeckBinding
import com.android.yugioh.domain.data.Card
import com.android.yugioh.ui.viewmodel.AddToDeckDialogViewModel
import com.android.yugioh.ui.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DialogAddToDeck : DialogFragment(R.layout.fragment_dialog_add_to_deck) {

	private val activity: AppCompatActivity by lazy { requireActivity() as AppCompatActivity }
	private var _dialogBinding: FragmentDialogAddToDeckBinding? = null
	private val dialogBinding: FragmentDialogAddToDeckBinding get() = _dialogBinding!!
	private val viewModel: CardViewModel by activityViewModels()
	private val dialogViewModel: AddToDeckDialogViewModel by viewModels()
	private lateinit var clickedCard: Card
	private val deckAdapter = DeckAdapter()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity.supportActionBar?.hide()
		isCancelable = false
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_dialogBinding = FragmentDialogAddToDeckBinding.inflate(inflater, container, false)
		clickedCard = viewModel.clickedCard
		return dialogBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupListeners()
		dialogBinding.apply {
			rvDecks.adapter = deckAdapter
			btnClose.setOnClickListener {
				dismiss()
			}
			viewLifecycleOwner.lifecycleScope.launch {
				viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
					launch {
						dialogViewModel.finalState.collect { state ->
							renderState(state)
						}
					}
					launch {
						dialogViewModel.events.collect { event ->
							when (event) {
								is DeckDialogEvent.ShowToast -> {
									Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
										.show()
								}
							}
						}
					}
				}
			}

		}
	}

	private fun setupListeners() {
		dialogBinding.etNewDeckName.addTextChangedListener { text ->
			dialogBinding.btnCreate.isEnabled = !text.isNullOrBlank()
		}

		dialogBinding.btnAdd.setOnClickListener {
			dialogViewModel.onAddClicked()
		}
		dialogBinding.btnClose.setOnClickListener {
			dismiss()
		}
		dialogBinding.btnCancelCreate.setOnClickListener {
			dialogViewModel.onCancelClicked()
			dialogBinding.etNewDeckName.text?.clear()
			hideKeyboard()
		}
		dialogBinding.btnCreate.setOnClickListener {
			val name = dialogBinding.etNewDeckName.text.toString()
			dialogViewModel.createDeck(name)
			dialogBinding.etNewDeckName.text?.clear()
			hideKeyboard()
		}
	}

	private fun renderState(state: DeckDialogState) {
		fun updateVisibility(
			isLoading: Boolean = false,
			isListMode: Boolean = false,
			isCreateMode: Boolean = false,
			isEmpty: Boolean = false
		) {
			dialogBinding.progressBar.isVisible = isLoading
			dialogBinding.rvDecks.isVisible = isListMode && !isEmpty
			dialogBinding.btnAdd.isVisible = isListMode
			dialogBinding.btnClose.isVisible = isListMode
			dialogBinding.tvEmptyMessage.isVisible = isEmpty
			dialogBinding.tilNewDeckName.isVisible = isCreateMode
			dialogBinding.btnCreate.isVisible = isCreateMode
			dialogBinding.btnCancelCreate.isVisible = isCreateMode
		}

		when (state) {
			is DeckDialogState.Loading -> {
				updateVisibility(isLoading = true)
			}

			is DeckDialogState.Success -> {
				updateVisibility(isListMode = true)
				deckAdapter.submitList(state.decks)
			}

			is DeckDialogState.Empty -> {
				updateVisibility(isListMode = true, isEmpty = true)
				deckAdapter.submitList(emptyList())
			}

			is DeckDialogState.CreateNew -> {
				updateVisibility(isCreateMode = true)
				dialogBinding.btnCreate.isEnabled =
					!dialogBinding.etNewDeckName.text.isNullOrBlank()
				dialogBinding.etNewDeckName.requestFocus()
			}

			is DeckDialogState.Creating -> {
				updateVisibility(isLoading = true)
			}
		}
	}

	private fun hideKeyboard() {
		val inputMethodManager =
			requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
		inputMethodManager?.hideSoftInputFromWindow(dialogBinding.etNewDeckName.windowToken, 0)
	}

}
