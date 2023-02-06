package com.android.yugioh.ui.view.fragment

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.android.yugioh.ui.view.MyFragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainNavHostFragment : NavHostFragment() {

	@Inject
	@JvmSuppressWildcards(true)
	lateinit var factory: MyFragmentFactory
	
	override fun onAttach(context: Context) {
		super.onAttach(context)
		childFragmentManager.fragmentFactory = factory
	}
}