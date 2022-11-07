package com.android.yugioh.ui.view

import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.android.yugioh.ui.view.dialog.DialogAdvancedSearch
import com.squareup.picasso.Picasso
import javax.inject.Inject

class MyFragmentFactory @Inject constructor(
	private val picasso: Picasso,
	private val adapters: Array<Array<ArrayAdapter<*>>>
) :
	FragmentFactory() {
	
	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when (className) {
			ListCardFragment::class.java.name -> ListCardFragment(picasso)
			DialogAdvancedSearch::class.java.name -> DialogAdvancedSearch(adapters)
			else -> super.instantiate(classLoader, className)
		}
	}
}