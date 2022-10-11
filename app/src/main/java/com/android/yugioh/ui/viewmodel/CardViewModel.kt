package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yugioh.model.api.CardProvider
import com.android.yugioh.model.data.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(private val service: CardProvider) : ViewModel() {
	
	/*private val service = CardProvider()*/
	
	private val currentList: MutableList<Card> = mutableListOf()
	
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()
	val searchCache: Map<String, List<Card>>
		get() = searchData
	
	private val cardListModel: MutableLiveData<List<Card>> = MutableLiveData()
	val mainList: LiveData<List<Card>>
		get() = cardListModel
	
	private val filterListModel: MutableLiveData<List<Card>> = MutableLiveData()
	val filterList: LiveData<List<Card>>
		get() = filterListModel
	
	private val modelProgressBar: MutableLiveData<Boolean> = MutableLiveData()
	val isLoading: LiveData<Boolean>
		get() = modelProgressBar
	
	private val modelMessageLoading: MutableLiveData<Boolean> = MutableLiveData()
	val isSearching: LiveData<Boolean>
		get() = modelMessageLoading
	
	
	fun getListRandomCards(): Job {
		return viewModelScope.launch {
			modelProgressBar.postValue(false)
			service.getListRandomCards()?.let {
				with(currentList) {
					addAll(it)
					cardListModel.postValue(this)
					filterListModel.postValue(this)
				}
			}
			modelProgressBar.postValue(true)
		}
	}
	
	fun searchCard(query: String) {
		viewModelScope.launch {
			modelProgressBar.postValue(filterListModel.value!!.isEmpty())
			filterListModel.value = (
				searchData[query]?.also {
					modelMessageLoading.postValue(true)
				} ?: service.searchCard(query)?.let {
					modelMessageLoading.postValue(true)
					if (it.isNotEmpty())
						searchData[query] = it
					it
				}
			)
			modelProgressBar.postValue(true)
		}
	}
	
	fun getFilterList(query: String) {
		currentList.run {
			filterListModel.postValue(this)
			if (query.isBlank()) { //User´s click x button //this can replace with isEmpty()
				modelMessageLoading.postValue(true)
				return
			}
			filterListModel.postValue(filter {
				it.name.contains(query, true)
			}.also {
				modelMessageLoading.postValue(it.isNotEmpty())
			})
		}
	}
	
	override fun onCleared() {
		CardProvider.isInit = false
		super.onCleared()
	}
	
}