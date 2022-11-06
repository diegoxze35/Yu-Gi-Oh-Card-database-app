package com.android.yugioh.ui.view

import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.android.yugioh.ui.view.dialog.DialogAdvancedSearch
import javax.inject.Inject

class MyFragmentFactory @Inject constructor(private val adapters: Array<ArrayAdapter<*>>) :
	FragmentFactory() {
	
	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when (className) {
			DialogAdvancedSearch::class.java.name -> DialogAdvancedSearch(adapters)
			else -> super.instantiate(classLoader, className)
		}
	}
}