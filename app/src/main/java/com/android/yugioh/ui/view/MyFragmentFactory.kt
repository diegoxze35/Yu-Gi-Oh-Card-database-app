package com.android.yugioh.ui.view

import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.android.yugioh.ui.view.dialog.DialogAdvancedSearch
import com.android.yugioh.ui.view.fragment.ListCardFragment
import com.squareup.picasso.Picasso
import javax.inject.Inject
import javax.inject.Named

class MyFragmentFactory @Inject constructor(
	private val picasso: Picasso,
	@Named("archetypes") private val archetypes: List<String>,
	private val adapters: Map<String, @JvmSuppressWildcards(true) ArrayAdapter<@JvmSuppressWildcards(
		true
	) Any>>
) : FragmentFactory() {

	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when (className) {
			ListCardFragment::class.java.name -> ListCardFragment(picasso)
			DialogAdvancedSearch::class.java.name -> DialogAdvancedSearch(archetypes, adapters)
			else -> super.instantiate(classLoader, className)
		}
	}
}