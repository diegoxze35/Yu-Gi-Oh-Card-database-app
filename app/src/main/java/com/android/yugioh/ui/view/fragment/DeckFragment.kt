package com.android.yugioh.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.yugioh.databinding.FragmentDeckBinding

class DeckFragment : Fragment() {

	//private val viewModel: DeckViewModel by viewModels()
	private var _decksBinding: FragmentDeckBinding? = null
	private val decksBinding: FragmentDeckBinding get() = _decksBinding!!

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_decksBinding = FragmentDeckBinding.inflate(layoutInflater, container, false)
		return decksBinding.root
	}
}