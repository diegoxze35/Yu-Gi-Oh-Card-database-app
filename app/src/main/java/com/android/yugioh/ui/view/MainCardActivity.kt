package com.android.yugioh.ui.view

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
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
	private lateinit var iconToolbar: ImageView
	private lateinit var toolbar: Toolbar
	private lateinit var navController: NavController
	private lateinit var linearLayout: LinearLayout
	private val titleBar by lazy {
		TextView(this).apply {
			setTextColor(ContextCompat.getColor(this.context, R.color.white))
			textSize = 18F //this should be dimen resource
			maxLines = 2
			typeface = Typeface.DEFAULT_BOLD
			isGone = true
		}
	}
	private val textMessageLoading by lazy {
		linearLayout.findViewById<TextView>(R.id.text_view_message)
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_card)
		linearLayout = findViewById(R.id.linear_layout_container)
		initToolbar()
		viewModel.isLoading.observe(this) { isGone ->
			textMessageLoading.text =
				getString(if (!viewModel.canAddFilterList) R.string.loading_message else R.string.searching_message)
			linearLayout.isGone = isGone
		}
	}
	
	private fun initToolbar() {
		toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar.also {
			searchView = it.findViewById(R.id.searchView)
			iconToolbar = it.findViewById(R.id.icon_toolbar)
			it.addView(titleBar)
		})
		supportActionBar?.let {
			title = null
		}
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
	
	fun updateToolbar(currentFragment: Fragment) {
		val fragmentMap = mapOf(
			(currentFragment is ListCardFragment) to {
				titleBar.isGone = true
				searchView.isGone = false
				iconToolbar.isGone = false
			}, (currentFragment is CardInfoFragment) to {
				titleBar.apply {
					text = viewModel.currentCard.value!!.name
					isGone = false
				}
				searchView.isGone = true
				iconToolbar.isGone = true
			}
		)
		fragmentMap[true]?.invoke()
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
			R.id.advanced_search_options -> {
				navController.navigate(R.id.action_listCardFragment_to_dialogAdvancedSearch)
				true
			}
			R.id.my_decks_option -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}