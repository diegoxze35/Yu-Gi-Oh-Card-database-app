package com.android.yugioh.ui.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.model.data.Card
import com.android.yugioh.ui.viewmodel.CardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListCardFragment : Fragment() {
	
	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	private lateinit var recyclerView: RecyclerView
	private lateinit var toolbar: Toolbar
	private lateinit var searchView: SearchView
	private lateinit var adapter: CardAdapter
	private lateinit var messageSearch: TextView
	private val gridLayout = GridLayoutManager(context, 2)
	private val linearLayout = LinearLayoutManager(context)
	private val viewModel: CardViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?, savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_list_card, container, false)
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		messageSearch = view.findViewById(R.id.textViewSearch)
		toolbar = view.findViewById(R.id.include)
		(activity as AppCompatActivity).setSupportActionBar(toolbar.also {
			searchView = it.findViewById(R.id.searchView)
		})
		searchView.apply {
			isSubmitButtonEnabled = true
			setOnQueryTextListener(object : SearchView.OnQueryTextListener {
				
				override fun onQueryTextSubmit(query: String): Boolean {
					messageSearch.text = resources.getString(R.string.searching_message)
					viewModel.searchCard(query.trim().lowercase())
					return true
				}
				
				override fun onQueryTextChange(newText: String): Boolean {
					if (loading.isActive) return false
					val sendText = newText.trim().lowercase()
					viewModel.searchCache[sendText]?.let { list -> //restore to memory
						messageSearch.isGone = true
						adapter.addFilterList(list)
						return true
					}
					viewModel.getFilterList(sendText)
					return true
				}
			})
		}
		
		recyclerView = view.findViewById(R.id.recyclerViewCard)
		recyclerView.apply {
			this@ListCardFragment.adapter =
				CardAdapter(viewModel.mainList.value?.toMutableList() ?: kotlin.run {
					loading = viewModel.getListRandomCards()
					mutableListOf()
				}, this@ListCardFragment::onClickCard).also {
					this.adapter = it
				}
			addOnScrollListener(object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					if (!loading.isActive) {
						searchView.clearFocus()
						if (searchView.query.isNotEmpty()) return
						if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL))
							loading = viewModel.getListRandomCards()
					}
				}
			})
		}
		
		viewModel.mainList.observe(viewLifecycleOwner) {
			adapter.addListCard(it)
		}
		viewModel.filterList.observe(viewLifecycleOwner) {
			if (viewModel.mainList.value!! == it) {
				if (searchView.query.isEmpty() && searchView.hasFocus()) //restore original list
					adapter.addFilterList(it)
				return@observe
			}
			adapter.addFilterList(it)
		}
		viewModel.isSearching.observe(viewLifecycleOwner) {
			messageSearch.apply {
				isGone = it
				text = resources.getString(R.string.search_message)
				if (it && viewModel.filterList.value!!.isEmpty()) {
					isGone = false
					text = resources.getString(R.string.not_result_search)
				}
			}
		}
	}
	
	override fun onResume() {
		super.onResume()
		searchView.clearFocus()
		with(resources.configuration.orientation) {
			if (this == Configuration.ORIENTATION_LANDSCAPE)
				recyclerView.layoutManager = gridLayout
			else if (this == Configuration.ORIENTATION_PORTRAIT)
				recyclerView.layoutManager = linearLayout
		}
	}
	
	private fun onClickCard(card: Card) = viewModel.onClickCard(card)
	
}