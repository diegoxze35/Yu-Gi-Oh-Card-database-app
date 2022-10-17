package com.android.yugioh.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel

class FragmentMessageLoading : Fragment() {
	
	private lateinit var progressBar: ProgressBar
	private lateinit var messageLoading: TextView
	
	private val viewModel: CardViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		return inflater.inflate(R.layout.fragment_message_loading, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		progressBar = view.findViewById(R.id.progressBar)
		messageLoading = view.findViewById(R.id.textViewLoading)
		
		viewModel.isLoading.observe(viewLifecycleOwner) {
			progressBar.isGone = it
			messageLoading.isGone = it
			messageLoading.apply {
				isGone = it
				text =
					if ((viewModel.mainList.value == viewModel.filterList.value))
						resources.getString(R.string.loading_message)
					else
						resources.getString(R.string.searching_message)
			}
		}
	}
}