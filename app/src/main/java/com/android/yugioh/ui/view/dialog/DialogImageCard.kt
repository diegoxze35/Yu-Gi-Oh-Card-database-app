package com.android.yugioh.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.yugioh.R
import com.android.yugioh.databinding.DialogImageCardBinding
import com.android.yugioh.ui.viewmodel.CardViewModel
import com.squareup.picasso.Picasso

class DialogImageCard(private val picasso: Picasso) : DialogFragment(R.layout.dialog_image_card) {

	private var _dialogImageCardBinding: DialogImageCardBinding? = null
	private val dialogImageCardBinding get() = _dialogImageCardBinding!!
	private val viewModel: CardViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_dialogImageCardBinding = DialogImageCardBinding.inflate(inflater)
		return dialogImageCardBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		dialogImageCardBinding.apply {
			with(resources.displayMetrics) {
				root.minimumWidth = widthPixels
				root.minimumHeight = heightPixels
			}
			root.setBackgroundColor(
				ContextCompat.getColor(
					requireContext(),
					R.color.color_border_card2
				)
			)
			picasso.load(viewModel.clickedCard.cardImages[0].imageUrl).noFade().into(root)
		}
		super.onViewCreated(view, savedInstanceState)
	}

	override fun onDestroy() {
		_dialogImageCardBinding = null
		super.onDestroy()
	}
}