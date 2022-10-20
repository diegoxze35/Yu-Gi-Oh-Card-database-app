package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
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
	private var isQuery = false
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()
	
	private val mainListLiveData: MutableLiveData<List<Card>> = MutableLiveData(listOf())
	val mainList: LiveData<List<Card>> get() = mainListLiveData
	
	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard
	
	private val currentQueryLiveData: MutableLiveData<String> = MutableLiveData()
	val currentQuery: String
		get() {
			return currentQueryLiveData.value.orEmpty()
		}
	
	fun setQuerySearch(query: String, onQuery: Boolean) {
		isQuery = onQuery
		currentQueryLiveData.value = query
	}
	
	val filterListLiveData: LiveData<List<Card>> =
		Transformations.switchMap(currentQueryLiveData) { query ->
			liveData<List<Card>> {
				if (loading.isActive) return@liveData
				if (!isQuery) {
					mainList.value?.filter {
						it.name.contains(query, true)
					}?.also {
						isSearchingLiveData.postValue(it.isNotEmpty())
						emit(it)
						return@liveData
					}
				}
				isLoadingLiveData.postValue(false)
				isSearchingLiveData.postValue(true)
				searchData[query]?.let {
					isLoadingLiveData.postValue(true)
					emit(it)
					return@liveData
				}
				service.searchCard(query)?.let {
					if (it.isNotEmpty())
						searchData[query] = it
					isLoadingLiveData.postValue(true)
					emit(it)
				}
			}
		}
	
	private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
	val isLoading: LiveData<Boolean> get() = isLoadingLiveData
	
	private val isSearchingLiveData: MutableLiveData<Boolean> = MutableLiveData()
	val isSearching: LiveData<Boolean> get() = isSearchingLiveData
	
	
	fun getListRandomCards() {
		if (loading.isActive) return
		loading = viewModelScope.launch {
			isLoadingLiveData.postValue(false)
			service.getListRandomCards()?.let {
				mainListLiveData.postValue(mainListLiveData.value!!.plus(it))
			}
			isLoadingLiveData.postValue(true)
		}
	}
	
	fun onClickCard(card: Card) = clickedCard.postValue(card)
	
	override fun onCleared() {
		CardProvider.isInit = false
		super.onCleared()
	}
	
}