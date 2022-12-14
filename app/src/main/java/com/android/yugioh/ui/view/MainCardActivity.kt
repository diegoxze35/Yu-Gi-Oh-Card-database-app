package com.android.yugioh.ui.view

import android.app.Dialog
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.setPadding
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.yugioh.databinding.ActivityMainCardBinding
import com.android.yugioh.model.data.Card
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	private val viewModel: CardViewModel by viewModels()
	private lateinit var mainBinding: ActivityMainCardBinding
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
	private val colorOK by lazy {
		ContextCompat.getColor(this, R.color.internet_OK)
	}
	private val colorFailure by lazy {
		ContextCompat.getColor(this, R.color.internet_not_OK)
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mainBinding = ActivityMainCardBinding.inflate(layoutInflater)
		setContentView(mainBinding.root)
		initToolbar()
		viewModel.networkManager.observe(this) { isOnline ->
			if (isOnline) {
				mainBinding.apply {
					textInternetIndicator.text = getString(R.string.connected_to_internet)
					layoutMessageInternet.apply {
						setBackgroundColor(colorOK)
						postDelayed({
							isGone = true
						}, 3000L)
					}
				}
				viewModel.getListRandomCards()
			} else {
				mainBinding.apply {
					textInternetIndicator.text = getString(R.string.not_connected_to_internet)
					layoutMessageInternet.apply {
						setBackgroundColor(colorFailure)
						isGone = false
					}
				}
			}
		}
	}
	
	private fun initToolbar() {
		setSupportActionBar(mainBinding.toolbar.also { it.addView(titleBar) })
		supportActionBar?.let { title = null }
		mainBinding.mainFragmentContainer.getFragment<MainNavHostFragment>().also {
			navController = it.navController.apply {
				addOnDestinationChangedListener { _, destination, _ ->
					MainScope().launch {
						updateToolbar(destination.id)
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
	
	fun startDetailFragment(card: Card, imageCard: Drawable?) {
		clearFocus()
		viewModel.onClickCard(card, imageCard)
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
				MainScope().launch {
					val dialog = Dialog(this@MainCardActivity).apply {
						setCancelable(false)
						setContentView(
							ProgressBar(this@MainCardActivity).apply {
								setPadding(24)
							}
						)
						show()
					}
					viewModel.getAllArchetypes().join()
					dialog.dismiss()
					navController.navigate(R.id.action_listCardFragment_to_dialogAdvancedSearch)
				}
				true
			}
			R.id.my_decks_option -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
	
}