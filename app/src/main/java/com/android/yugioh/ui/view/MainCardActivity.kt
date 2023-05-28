package com.android.yugioh.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.yugioh.databinding.ActivityMainCardBinding
import com.android.yugioh.domain.SearchCardByNameOfflineUseCase
import com.android.yugioh.domain.SearchCardByNameOnlineUseCase
import com.android.yugioh.domain.data.Card
import com.android.yugioh.ui.view.fragment.MainNavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	companion object {
		private const val DELAY_INTERNET_MESSAGE = 3000L
	}

	@Inject
	lateinit var offlineSearchUseCase: SearchCardByNameOfflineUseCase

	@Inject
	lateinit var onlineSearchUseCase: SearchCardByNameOnlineUseCase

	private val viewModel: CardViewModel by viewModels()
	val mainBinding: ActivityMainCardBinding by lazy {
		ActivityMainCardBinding.inflate(layoutInflater)
	}
	private lateinit var navController: NavController
	private val colorOK by lazy {
		ContextCompat.getColor(this, R.color.internet_OK)
	}
	private val colorFailure by lazy {
		ContextCompat.getColor(this, R.color.internet_not_OK)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
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

		addMenuProvider(object : MenuProvider {
			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.menu_main_activity, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				return when (menuItem.itemId) {
					R.id.advanced_search_options -> {
						navController.currentBackStackEntry?.let {
							navController.navigate(
								if (it.destination.id == R.id.listCardFragment)
									R.id.action_listCardFragment_to_dialogAdvancedSearch
								else
									R.id.action_cardInfoFragment_to_dialogAdvancedSearch
							)
							return true
						} ?: return false
					}
					R.id.my_decks_option -> true
					else -> false
				}
			}
		})
	}

	private fun initToolbar() {
		setSupportActionBar(mainBinding.toolbar)
		supportActionBar?.title = null
		mainBinding.mainFragmentContainer.getFragment<MainNavHostFragment>().also {
			navController = it.navController
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
					true
				}
			})
		}
	}

	fun startDetailFragment(card: Card) {
		mainBinding.searchView.clearFocus()
		viewModel.onClickCard(card)
		navController.navigate(R.id.action_listCardFragment_to_cardInfoFragment)
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}
}