package com.android.yugioh.ui.view.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.transition.TransitionInflater
import com.android.yugioh.R
import com.android.yugioh.databinding.FragmentImageCardBinding
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.squareup.picasso.Picasso

class CardImageFragment(private val picasso: Picasso) : Fragment(R.layout.fragment_image_card) {

	private var _fragmentImageCardBinding: FragmentImageCardBinding? = null
	private val fragmentImageCardBinding get() = _fragmentImageCardBinding!!
	private val viewModel: CardViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_fragmentImageCardBinding = FragmentImageCardBinding.inflate(inflater)
		return fragmentImageCardBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val menuHost: MenuHost = requireActivity()
		menuHost.addMenuProvider(object : MenuProvider {
			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				/*TODO()*/
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				/*TODO()*/
				return false
			}
		}, viewLifecycleOwner, Lifecycle.State.RESUMED)
		fragmentImageCardBinding.root.apply {
			with(resources.displayMetrics) {
				minimumWidth = widthPixels
				minimumHeight = heightPixels
			}
			setBackgroundColor(
				ContextCompat.getColor(
					requireContext(),
					viewModel.clickedCard.type.color
				)
			)
			sharedElementEnterTransition = TransitionInflater.from(requireContext())
				.inflateTransition(R.transition.transition_card_image)
			postponeEnterTransition()
			transitionName = viewModel.clickedCard.name
			picasso.load(viewModel.clickedCard.cardImages[0].imageUrl).noFade().into(this)
			startPostponedEnterTransition()
		}
		super.onViewCreated(view, savedInstanceState)
	}

	override fun onDestroy() {
		_fragmentImageCardBinding = null
		super.onDestroy()
	}
}