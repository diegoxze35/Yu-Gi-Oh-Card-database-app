package com.android.yugioh.ui.view

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.TextView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.android.yugioh.databinding.ActivityMainCardBinding
import com.android.yugioh.model.data.Card
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	companion object {
		private const val DURATION = 400L
	}
	
	private lateinit var mainBinding: ActivityMainCardBinding
	private val viewModel: CardViewModel by viewModels()
	private lateinit var navController: NavController
	private val titleBar by lazy {
		TextView(this).apply {
			setTextColor(ContextCompat.getColor(this.context, R.color.white))
			textSize = 18F //this should be dimen resource
			maxLines = 2
			typeface = Typeface.DEFAULT_BOLD
			isGone = true
		}
	}
	private val transition by lazy {
		Slide(Gravity.BOTTOM).apply {
			duration = DURATION
			addTarget(mainBinding.linearLayoutContainer)
		}
	}
	
	@Inject
	lateinit var factory: MyFragmentFactory
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mainBinding = ActivityMainCardBinding.inflate(layoutInflater)
		setContentView(mainBinding.root)
		initToolbar()
		viewModel.isLoading.observe(this) { isGone ->
			mainBinding.textViewMessage.text =
				getString(if (!viewModel.canAddFilterList) R.string.loading_message else R.string.searching_message)
			TransitionManager.beginDelayedTransition(mainBinding.root, transition)
			mainBinding.linearLayoutContainer.isGone = isGone
		}
	}
	
	private fun initToolbar() {
		setSupportActionBar(mainBinding.toolbar.also {
			it.addView(titleBar)
		})
		supportActionBar?.let { title = null }
		(supportFragmentManager.findFragmentById(R.id.fragment_container_1) as NavHostFragment).also {
			it.childFragmentManager.fragmentFactory = factory
			navController = it.navController.apply {
				addOnDestinationChangedListener { _, destination, _ ->
					MainScope().launch {
						updateToolbar(
							destination.id
						)
					}
				}
			}
		}
		setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
		mainBinding.searchView.apply {
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
	
	private fun updateToolbar(destinationId: Int) {
		val fragmentMap = mapOf(
			R.id.listCardFragment to {
				mainBinding.apply {
					titleBar.isGone = true
					searchView.isGone = false
					iconToolbar.isGone = false
				}
			},
			R.id.cardInfoFragment to {
				titleBar.apply {
					text = viewModel.currentCard.value!!.name
					isGone = false
				}
				mainBinding.apply {
					searchView.isGone = true
					iconToolbar.isGone = true
				}
			}
		)
		fragmentMap[destinationId]?.invoke()
		
	}
	
	fun startDetailFragment(card: Card) {
		clearFocus()
		viewModel.onClickCard(card)
		navController.navigate(R.id.action_listCardFragment_to_cardInfoFragment)
	}
	
	fun clearFocus() = mainBinding.searchView.clearFocus()
	
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