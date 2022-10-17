package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yugioh.model.api.CardProvider
import com.android.yugioh.model.data.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(private val service: CardProvider) : ViewModel() {
	
	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	
	private val currentList: MutableList<Card> = mutableListOf()
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()
	
	private val mainCardListLiveData: MutableLiveData<List<Card>> = MutableLiveData()
	val mainList: LiveData<List<Card>>
		get() = mainCardListLiveData
	
	private val filterListLiveData: MutableLiveData<List<Card>> = MutableLiveData()
	val filterList: LiveData<List<Card>>
		get() = filterListLiveData
	
	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard
	
	private val isLoadingState: MutableLiveData<Boolean> = MutableLiveData()
	val isLoading: LiveData<Boolean>
		get() = isLoadingState
	
	private val isSearchingState: MutableLiveData<Boolean> = MutableLiveData()
	val isSearching: LiveData<Boolean>
		get() = isSearchingState
	
	fun getListRandomCards() {
		if (loading.isActive) return
		loading = viewModelScope.launch {
			isLoadingState.postValue(false)
			service.getListRandomCards()?.let {
				with(currentList) {
					addAll(it)
					mainCardListLiveData.postValue(this)
					filterListLiveData.postValue(this)
				}
			}
			isLoadingState.postValue(true)
		}
	}
	
	fun searchCard(query: String) {
		if (loading.isActive) return
		loading = viewModelScope.launch {
			isLoadingState.postValue(/*filterListLiveData.value!!.isEmpty()*/false)
			filterListLiveData.value = (
				searchData[query]?.also {
					isSearchingState.postValue(true)
				} ?: service.searchCard(query)?.let {
					isSearchingState.postValue(true)
					if (it.isNotEmpty())
						searchData[query] = it
					it
				}
			)
			isLoadingState.postValue(true)
		}
	}
	
	fun onClickCard(card: Card) = clickedCard.postValue(card)
	
	fun getFilterList(query: String) {
		if (loading.isActive) return
		searchData[query]?.let {
			filterListLiveData.postValue(it) //restore to memory
			return
		}
		currentList.run {
			filterListLiveData.postValue(this)
			if (query.isBlank()) { //User´s click x button //this can replace with isEmpty()
				isSearchingState.postValue(true)
				return
			}
			filterListLiveData.postValue(filter {
				it.name.contains(query, true)
			}.also {
				isSearchingState.postValue(it.isNotEmpty())
			})
		}
	}
	
	override fun onCleared() {
		CardProvider.isInit = false
		super.onCleared()
	}
	
}