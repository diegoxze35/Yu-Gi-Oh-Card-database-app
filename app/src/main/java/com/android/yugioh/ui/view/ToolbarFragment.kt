package com.android.yugioh.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import com.android.yugioh.R
import com.android.yugioh.ui.viewmodel.CardViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ToolbarFragment : Fragment() {
	
	private var param1: String? = null
	private var param2: String? = null
	private lateinit var searchView: SearchView
	private lateinit var toolbar: Toolbar
	
	private val viewModel: CardViewModel by activityViewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			param1 = it.getString(ARG_PARAM1)
			param2 = it.getString(ARG_PARAM2)
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_toolbar, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		toolbar = view.findViewById(R.id.toolbar)
		(activity as AppCompatActivity).setSupportActionBar(toolbar.also {
			searchView = it.findViewById(R.id.searchView)
		})
		searchView.apply {
			isSubmitButtonEnabled = true
			setOnQueryTextListener(object : SearchView.OnQueryTextListener {
				override fun onQueryTextSubmit(query: String): Boolean {
					viewModel.setQuerySearch(query.trim().lowercase(), true)
					return true
				}
				override fun onQueryTextChange(newText: String): Boolean {
					viewModel.setQuerySearch(newText.trim(), false)
					return true
				}
			})
		}
	}
		
		companion object {
			/**
			 * Use this factory method to create a new instance of
			 * this fragment using the provided parameters.
			 *
			 * @param param1 Parameter 1.
			 * @param param2 Parameter 2.
			 * @return A new instance of fragment ToolbarFragment.
			 */
			@JvmStatic
			fun newInstance(param1: String, param2: String) =
				ToolbarFragment().apply {
					arguments = Bundle().apply {
						putString(ARG_PARAM1, param1)
						putString(ARG_PARAM2, param2)
					}
				}
		}
	}