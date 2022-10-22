package com.android.yugioh.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.android.yugioh.model.data.Card
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	companion object {
		const val NAME_FRAGMENT_LIST = "NAME_FRAGMENT_LIST"
	}
	
	private val viewModel: CardViewModel by viewModels()
	private val fragmentDetail = CardInfoFragment()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_card)
		/*viewModel.currentCard.observe(this) {
			supportFragmentManager.commit {
				setReorderingAllowed(true)
				setCustomAnimations(
					R.anim.alpha_in,
					R.anim.alpha_out,
					R.anim.alpha_in,
					R.anim.alpha_out
				)
				replace(R.id.fragment_container_1, fragmentDetail.also { cardInfoFragment ->
					cardInfoFragment.card = it
				})
				addToBackStack(NAME_FRAGMENT_LIST)
			}
		}*/
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