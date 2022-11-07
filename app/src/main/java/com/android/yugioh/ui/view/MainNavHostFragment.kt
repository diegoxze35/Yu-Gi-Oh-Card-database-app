package com.android.yugioh.ui.view

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainNavHostFragment : NavHostFragment() {
	
	@Inject
	lateinit var factory: MyFragmentFactory
	
	override fun onAttach(context: Context) {
		super.onAttach(context)
		childFragmentManager.fragmentFactory = factory
	}
}