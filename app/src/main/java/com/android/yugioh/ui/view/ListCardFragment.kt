package com.android.yugioh.ui.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel

class ListCardFragment : Fragment() {
	
	private lateinit var recyclerView: RecyclerView
	private lateinit var adapter: CardAdapter
	private lateinit var messageSearch: TextView
	private val activity by lazy {
		requireActivity() as MainCardActivity
	}
	
	private val viewModel: CardViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?, savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_list_card, container, false)
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		messageSearch = view.findViewById(R.id.textViewSearch)
		recyclerView = view.findViewById(R.id.recyclerViewCard)
		recyclerView.apply {
			this@ListCardFragment.adapter =
				CardAdapter(
					with(viewModel.mainList.value!!) {
						if (isNotEmpty())
							toMutableList()
						else {
							viewModel.getListRandomCards()
							mutableListOf()
						}
					}, activity::startDetailFragment
				).also {
					this.adapter = it
				}
			addOnScrollListener(object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					activity.clearFocus()
					if (viewModel.currentQuery.isNotEmpty()) return
					if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL))
						viewModel.getListRandomCards()
				}
			})
		}
		
		viewModel.mainList.observe(viewLifecycleOwner) {
			adapter.addListCard(it)
		}
		
		viewModel.filterListLiveData.observe(viewLifecycleOwner) {
			adapter.addFilterList(it)
		}
		
		viewModel.isSearching.observe(viewLifecycleOwner) {
			messageSearch.apply {
				if (viewModel.filterListLiveData.value!!.isEmpty()) {
					isGone = false
					text = resources.getString(R.string.not_result_search)
				}
				isGone = it
				text = resources.getString(R.string.search_message)
			}
		}
	}
	
	override fun onResume() {
		super.onResume()
		activity.clearFocus()
		with(resources.configuration.orientation) {
			if (this == Configuration.ORIENTATION_LANDSCAPE)
				recyclerView.layoutManager = GridLayoutManager(context, 2)
			else if (this == Configuration.ORIENTATION_PORTRAIT)
				recyclerView.layoutManager = LinearLayoutManager(context)
		}
	}
	
}