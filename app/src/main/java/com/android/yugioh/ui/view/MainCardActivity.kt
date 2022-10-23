package com.android.yugioh.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.android.yugioh.model.data.Card
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	companion object {
		const val NAME_FRAGMENT_LIST = "NAME_FRAGMENT_LIST"
	}
	
	private val viewModel: CardViewModel by viewModels()
	private lateinit var searchView: SearchView
	private lateinit var toolbar: Toolbar
	private val fragmentDetail = CardInfoFragment()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_card)
		initToolbar()
	}
	
	private fun initToolbar() {
		toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar.also {
			searchView = it.findViewById(R.id.searchView)
		})
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
		viewModel.onClickCard(card)
		supportFragmentManager.commit {
			setReorderingAllowed(true)
			setCustomAnimations(
				R.anim.alpha_in,
				R.anim.alpha_out,
				R.anim.alpha_in,
				R.anim.alpha_out
			)
			replace(R.id.fragment_container_1, fragmentDetail)
			addToBackStack(NAME_FRAGMENT_LIST)
		}
	}
	
	fun clearFocus() = searchView.clearFocus()
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_main_activity, menu)
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return super.onOptionsItemSelected(item)
	}
	
	override fun onBackPressed() {
		with(supportFragmentManager) {
			if (backStackEntryCount > 0) {
				popBackStack()
				return
			}
		}
		super.onBackPressed()
	}
}