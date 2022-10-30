package com.android.yugioh.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.SearchView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.yugioh.model.data.Card
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	private val viewModel: CardViewModel by viewModels()
	private lateinit var searchView: SearchView
	private lateinit var toolbar: Toolbar
	private lateinit var navController: NavController
	private lateinit var linearLayout: LinearLayout
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_card)
		linearLayout = findViewById(R.id.linear_layout_container)
		initToolbar()
		viewModel.isLoading.observe(this) { isGone ->
			linearLayout.isGone = isGone
		}
	}
	
	private fun initToolbar() {
		toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar.also {
			searchView = it.findViewById(R.id.searchView)
		})
		val navHostFragment =
			supportFragmentManager.findFragmentById(R.id.fragment_container_1) as NavHostFragment
		navController = navHostFragment.navController
		setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
		searchView.apply {
			isSubmitButtonEnabled = true
			setOnQueryTextListener(object : SearchView.OnQueryTextListener {
				override fun onQueryTextSubmit(query: String): Boolean {
					viewModel.setQuerySearch(query.trim().lowercase(), true)
					return true
				}
				
				override fun onQueryTextChange(newText: String): Boolean {
					viewModel.setQuerySearch(newText.trim(), false)
					return true
				}
			})
		}
	}
	
	fun startDetailFragment(card: Card) {
		clearFocus()
		viewModel.onClickCard(card)
		navController.navigate(R.id.action_listCardFragment_to_cardInfoFragment)
	}
	
	fun clearFocus() = searchView.clearFocus()
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_main_activity, menu)
		return true
	}
	
	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.advanced_search_options -> true
			R.id.my_decks_option -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}