package com.android.yugioh.ui.view.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.android.yugioh.R
import com.android.yugioh.databinding.FragmentListCardBinding
import com.android.yugioh.ui.view.CardAdapter
import com.android.yugioh.ui.view.MainCardActivity
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.squareup.picasso.Picasso

class ListCardFragment(private val picasso: Picasso) : Fragment() {

	companion object {
		private const val DURATION = 400L
		private const val SPAN_COUNT = 2
	}

	private val viewModel: CardViewModel by activityViewModels()
	private var _listFragmentBinding: FragmentListCardBinding? = null
	private val listFragmentBinding: FragmentListCardBinding get() = _listFragmentBinding!!
	private lateinit var adapter: CardAdapter
	private val activity by lazy {
		requireActivity() as MainCardActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_listFragmentBinding = FragmentListCardBinding.inflate(inflater, container, false)
		with(activity) {
			mainBinding.searchView.isGone = false
			mainBinding.iconToolbar.isGone = false
			supportActionBar?.title = null
		}
		return listFragmentBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		listFragmentBinding.recyclerViewCard.apply {
			adapter = CardAdapter(picasso, activity::startDetailFragment).also {
				this@ListCardFragment.adapter = it
			}
			addOnScrollListener(object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					activity.mainBinding.searchView.clearFocus()
					if (viewModel.canAddFilterList) return
					if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL))
						viewModel.getListRandomCards()
				}
			})
		}

		viewModel.fragmentListLiveData.observe(viewLifecycleOwner) {
			if (!viewModel.canAddFilterList) adapter.submitList(it.loadListState.mainList)
			listFragmentBinding.textViewMessage.text = getString(
				if (!viewModel.canAddFilterList) R.string.loading_message
				else R.string.searching_message
			)
			TransitionManager.beginDelayedTransition(
				listFragmentBinding.root,
				Slide(Gravity.BOTTOM).apply {
					duration = DURATION
					addTarget(listFragmentBinding.linearLayoutContainer)
				}
			)
			listFragmentBinding.linearLayoutContainer.isGone = it.loadListState.isLoadingGone
			listFragmentBinding.textViewSearch.apply {
				isGone = it.searchingState.hideSearchMessage
				text = resources.getString(R.string.search_message)
				if (it.searchingState.searchNotResult) {
					isGone = false
					text = resources.getString(R.string.not_result_search)
				}
			}
		}
		viewModel.filterListLiveData.observe(viewLifecycleOwner) {
			if (viewModel.canAddFilterList) adapter.submitList(it)
		}
	}

	override fun onResume() {
		super.onResume()
		activity.mainBinding.searchView.clearFocus()
		with(resources.configuration.orientation) {
			if (this == Configuration.ORIENTATION_LANDSCAPE) listFragmentBinding.recyclerViewCard.layoutManager =
				GridLayoutManager(context, SPAN_COUNT)
			else if (this == Configuration.ORIENTATION_PORTRAIT) listFragmentBinding.recyclerViewCard.layoutManager =
				LinearLayoutManager(context)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_listFragmentBinding = null
	}

}