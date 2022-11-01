package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.android.yugioh.R


class DialogAdvancedSearch : DialogFragment(R.layout.dialog_advanced_search) {
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		isCancelable = false
		super.onViewCreated(view, savedInstanceState)
		view.findViewById<ImageButton>(R.id.image_button_close).setOnClickListener {
			dismiss()
		}
	}
}