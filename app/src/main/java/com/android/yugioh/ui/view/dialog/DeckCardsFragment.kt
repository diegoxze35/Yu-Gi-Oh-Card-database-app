package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.yugioh.R
import com.android.yugioh.databinding.FragmentDeckCardsBinding
import com.android.yugioh.ui.view.CardAdapter
import com.android.yugioh.ui.view.MainCardActivity
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.android.yugioh.ui.viewmodel.CardsDeckViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckCardsFragment(private val picasso: Picasso) : Fragment() {

	private val mainViewModel: CardViewModel by activityViewModels()
	private val deckCardsViewModel: CardsDeckViewModel by viewModels()
	private val args: DeckCardsFragmentArgs by navArgs()
	private val activity by lazy {
		requireActivity() as MainCardActivity
	}
	private lateinit var adapter: CardAdapter
	private var _deckCardsBinding: FragmentDeckCardsBinding? = null
	private val deckCardsBinding: FragmentDeckCardsBinding get() = _deckCardsBinding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		with(activity) {
			mainBinding.searchView.isGone = true
			mainBinding.iconToolbar.isGone = true
		}
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_deckCardsBinding = FragmentDeckCardsBinding.inflate(layoutInflater, container, false)
		return deckCardsBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		activity.mainBinding.toolbar.title = args.deckName
		adapter = CardAdapter(
			picasso,
			onCLick = {
				activity.mainBinding.searchView.clearFocus()
				mainViewModel.onClickCard(it)
				findNavController().navigate(R.id.action_deckCardsFragment_to_cardInfoFragment)
			}
		)
		deckCardsBinding.deckCards.adapter = adapter
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				deckCardsViewModel.allCards.collect {
					adapter.submitList(it)
				}
			}
		}
		deckCardsViewModel.getAllCards(args.deckId)
	}

}
