package com.android.yugioh.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.android.yugioh.databinding.FragmentDeckBinding
import com.android.yugioh.ui.view.DeckMetadataAdapter
import com.android.yugioh.ui.view.MainCardActivity
import com.android.yugioh.ui.viewmodel.DeckViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckFragment : Fragment() {

	private val viewModel: DeckViewModel by viewModels()
	private var _decksBinding: FragmentDeckBinding? = null
	private val decksBinding: FragmentDeckBinding get() = _decksBinding!!
	private lateinit var deckMetadataAdapter: DeckMetadataAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		with(requireActivity() as MainCardActivity) {
			mainBinding.searchView.isGone = true
			mainBinding.iconToolbar.isGone = true
		}
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_decksBinding = FragmentDeckBinding.inflate(layoutInflater, container, false)
		return decksBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		deckMetadataAdapter = DeckMetadataAdapter {
			val action = DeckFragmentDirections.actionDeckFragmentToDeckCardsFragment(
				deckName = it.name,
				deckId = it.id
			)
			findNavController().navigate(action)
		}
		decksBinding.recyclerViewDecks.adapter = deckMetadataAdapter
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.allDecks().collect {
					deckMetadataAdapter.submitList(it)
				}
			}
		}

	}

}
