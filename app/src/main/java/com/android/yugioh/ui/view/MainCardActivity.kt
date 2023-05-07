package com.android.yugioh.ui.view

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.android.yugioh.databinding.ActivityMainCardBinding
import com.android.yugioh.domain.SearchCardByNameOfflineUseCase
import com.android.yugioh.domain.SearchCardByNameOnlineUseCase
import com.android.yugioh.domain.data.Card
import com.android.yugioh.ui.view.fragment.MainNavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	companion object {
		private const val DURATION_FADE = 600L
		private const val DELAY_INTERNET_MESSAGE = 3000L
	}

	@Inject
	lateinit var offlineSearchUseCase: SearchCardByNameOfflineUseCase

	@Inject
	lateinit var onlineSearchUseCase: SearchCardByNameOnlineUseCase

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
	private val buttonCloseAdvancedSearch by lazy {
		ImageButton(this).apply {
			setImageResource(R.drawable.ic_baseline_arrow_back_24)
			setPadding(24, 0, 24, 0)
			setBackgroundColor(ContextCompat.getColor(this.context, R.color.main_color_app))
			setOnClickListener {
				viewModel.querySearch = ""
				viewModel.setSearchUseCase(offlineSearchUseCase.apply {
					from = viewModel.fragmentListLiveData.value!!.loadListState.mainList
				})
				this.isGone = true
				updateToolbar(R.id.listCardFragment)
			}
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
			mainBinding.apply {
				if (isOnline) {
					textInternetIndicator.text = getString(R.string.connected_to_internet)
					layoutMessageInternet.apply {
						setBackgroundColor(colorOK)
						postDelayed({
							isGone = true
						}, DELAY_INTERNET_MESSAGE)
					}
				} else {
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
		setSupportActionBar(mainBinding.toolbar.also {
			it.addView(buttonCloseAdvancedSearch)
			it.addView(titleBar)
		})
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
				override fun onQueryTextSubmit(query: String): Boolean = with(viewModel) {
					querySearch = query
					setSearchUseCase(onlineSearchUseCase)
					true
				}

				override fun onQueryTextChange(newText: String): Boolean = with(viewModel) {
					querySearch = newText
					if (isRemoteSearch && canAddFilterList) return false
					setSearchUseCase(
						offlineSearchUseCase.apply {
							from =
								viewModel.fragmentListLiveData.value!!.loadListState.mainList
						}
					)
					return true
				}
			})
		}
	}

	private fun updateToolbar(@IdRes destinationId: Int) {
		val updateTitleBar: (String) -> Unit = { newTitle ->
			mainBinding.apply {
				titleBar.apply {
					text = newTitle
					isGone = false
				}
				searchView.isGone = true
				iconToolbar.isGone = true
			}
		}
		val fragmentMap: Map<Int, () -> Unit> = mapOf(
			R.id.listCardFragment to {
				mainBinding.toolbar.isGone = false
				if (!viewModel.isRemoteSearchAdvance)
					mainBinding.apply {
						titleBar.isGone = true
						buttonCloseAdvancedSearch.isGone = true
						searchView.isGone = false
						iconToolbar.isGone = false
						buttonCloseAdvancedSearch.isGone = true
					}
				else {
					buttonCloseAdvancedSearch.isGone = false
					updateTitleBar(getString(R.string.results_of_search))
				}
			},
			R.id.cardInfoFragment to {
				updateTitleBar(viewModel.clickedCard.name)
				buttonCloseAdvancedSearch.isGone = true
			},
			R.id.dialogAdvancedSearch to {
				TransitionManager.beginDelayedTransition(
					mainBinding.toolbar, Fade().apply {
						duration = DURATION_FADE
					}
				)
				mainBinding.toolbar.isGone = true
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
				navController.navigate(R.id.action_listCardFragment_to_dialogAdvancedSearch)
				true
			}

			R.id.my_decks_option -> true
			else -> super.onOptionsItemSelected(item)
		}
	}

}