package com.android.yugioh.ui.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.isGone
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	private lateinit var recycler: RecyclerView
	private lateinit var progressBar: ProgressBar
	private lateinit var messageLoading: TextView
	private lateinit var toolbar: Toolbar
	private lateinit var guideline: Guideline
	private lateinit var adapter: CardAdapter
	private lateinit var searchView: SearchView
	private lateinit var messageSearch: TextView
	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	private val gridLayout = GridLayoutManager(this, 2)
	private val linearLayout = LinearLayoutManager(this)
	
	private val viewModel: CardViewModel by viewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_card)
		initComponents()
		
		/*Constructor is a Observer interface*/
		viewModel.mainList.observe(this) {
			adapter.addListCard(it)
		}
		viewModel.filterList.observe(this) {
			if (viewModel.mainList.value!! == it) {
				if (searchView.query.isEmpty() && searchView.hasFocus()) //restore original list
					adapter.addFilterList(it)
				return@observe
			}
			adapter.addFilterList(it)
		}
		viewModel.isLoading.observe(this) {
			progressBar.isGone = it
			messageLoading.isGone = it
			messageLoading.apply {
				isGone = it
				text =
					if ((viewModel.mainList.value == viewModel.filterList.value)) resources.getString(
						R.string.loading_message
					)
					else resources.getString(R.string.searching_message)
			}
		}
		viewModel.isSearching.observe(this) {
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
	
	private fun initComponents() {
		messageSearch = findViewById(R.id.textViewSearch)
		recycler = findViewById(R.id.recyclerViewCard)
		progressBar = findViewById(R.id.progressBar)
		messageLoading = findViewById(R.id.textViewLoading)
		toolbar = findViewById(R.id.include)
		guideline = findViewById(R.id.guideline)
		
		setSupportActionBar(toolbar.also {
			searchView = it.findViewById<SearchView>(R.id.searchView).apply {
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
		})
		
		recycler.apply {
			this@MainCardActivity.adapter =
				CardAdapter(viewModel.mainList.value?.toMutableList() ?: kotlin.run {
					loading = viewModel.getListRandomCards()
					mutableListOf()
				}).also {
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
	}
	
	override fun onResume() {
		super.onResume()
		searchView.clearFocus()
		with(resources.configuration.orientation) {
			if (this == Configuration.ORIENTATION_LANDSCAPE) {
				recycler.layoutManager = gridLayout
				guideline.setGuidelinePercent(0.85F)
			} else if (this == Configuration.ORIENTATION_PORTRAIT) {
				recycler.layoutManager = linearLayout
				guideline.setGuidelinePercent(0.92F)
				
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_main_activity, menu)
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		
		return super.onOptionsItemSelected(item)
	}
	
}