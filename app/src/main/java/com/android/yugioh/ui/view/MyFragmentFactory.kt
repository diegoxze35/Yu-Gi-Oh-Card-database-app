package com.android.yugioh.ui.view

import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.android.yugioh.domain.SearchCardByOptionsOlineUseCase
import com.android.yugioh.ui.view.dialog.DialogAdvancedSearch
import com.android.yugioh.ui.view.fragment.CardImageFragment
import com.android.yugioh.ui.view.fragment.CardInfoFragment
import com.android.yugioh.ui.view.fragment.ListCardFragment
import com.squareup.picasso.Picasso
import javax.inject.Inject
import javax.inject.Named

class MyFragmentFactory @Inject constructor(
	private val picasso: Picasso,
	@Named("archetypes") private val archetypes: List<String>,
	private val adapters: Map<String, @JvmSuppressWildcards(true) ArrayAdapter<@JvmSuppressWildcards(
		true
	) Any>>,
	private val advancedSearchUseCase: SearchCardByOptionsOlineUseCase
) : FragmentFactory() {

	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when (className) {
			ListCardFragment::class.java.name -> ListCardFragment(picasso)
			DialogAdvancedSearch::class.java.name -> DialogAdvancedSearch(archetypes, adapters, advancedSearchUseCase)
			CardInfoFragment::class.java.name -> CardInfoFragment(picasso)
			CardImageFragment::class.java.name -> CardImageFragment(picasso)
			else -> super.instantiate(classLoader, className)
		}
	}
}