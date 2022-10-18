package com.android.yugioh.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel
import androidx.activity.viewModels
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCardActivity : AppCompatActivity() {
	
	private val viewModel: CardViewModel by viewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_card)
		viewModel.currentCard.observe(this) {
			supportFragmentManager.commit {
				setReorderingAllowed(true)
				setCustomAnimations(R.anim.scale_enter_anim, R.anim.alpha_out)
				replace(R.id.fragment_container_1, CardInfoFragment(it))
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