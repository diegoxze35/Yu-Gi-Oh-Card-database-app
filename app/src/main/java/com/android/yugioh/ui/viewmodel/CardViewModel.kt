package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.android.yugioh.model.api.CardProvider
import com.android.yugioh.model.data.Card
import com.android.yugioh.ui.view.NetworkConnectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
	val networkManager: NetworkConnectivity,
	private val service: CardProvider
) : ViewModel() {
	
	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	private var isSubmit = false
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()
	private val mainListLiveData: MutableLiveData<List<Card>> = MutableLiveData(emptyList())
	val mainList: LiveData<List<Card>> get() = mainListLiveData
	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard
	private val currentQueryLiveData: MutableLiveData<String> = MutableLiveData()
	val canAddFilterList: Boolean
		get() {
			return currentQueryLiveData.value.orEmpty().isNotEmpty()
		}
	private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
	val isLoading: LiveData<Boolean> get() = isLoadingLiveData
	
	data class SearchingState(
		val hideSearchMessage: Boolean = true,
		val searchNotResult: Boolean = false
	)
	
	private val isSearchingLiveData: MutableLiveData<SearchingState> =
		MutableLiveData(SearchingState())
	val isSearching: LiveData<SearchingState> get() = isSearchingLiveData
	
	
	fun setQuerySearch(query: String, isSubmit: Boolean) {
		this.isSubmit = isSubmit
		currentQueryLiveData.value = query
	}
	
	val filterListLiveData: LiveData<List<Card>> =
		Transformations.switchMap(currentQueryLiveData) { query ->
			liveData<List<Card>> {
				if ((networkManager.value == false) && loading.isActive) return@liveData
				
				if (query.isEmpty()) { //restore original list with query is empty
					isSearchingLiveData.value = isSearchingLiveData.value?.copy(
						hideSearchMessage = true,
						searchNotResult = false
					)
					mainListLiveData.value = mainListLiveData.value //notify observer
					return@liveData
				}
				
				if (!isSubmit) {
					searchData[query]?.let { emit(it); return@liveData }
					mainList.value?.filter {
						it.name.contains(query, true)
					}?.also {
						isSearchingLiveData.value = isSearchingLiveData.value?.copy(
							hideSearchMessage = it.isNotEmpty(),
							searchNotResult = false
						)
						emit(it)
						return@liveData
					}
				}
				isLoadingLiveData.value = false
				searchData[query]?.let {
					isLoadingLiveData.value = true
					emit(it)
					return@liveData
				}
				service.searchCard(query)?.let {
					isSearchingLiveData.value = isSearchingLiveData.value?.copy(
						hideSearchMessage = it.isNotEmpty(),
						searchNotResult = it.isEmpty()
					)
					if (it.isNotEmpty()) searchData[query] = it
					isLoadingLiveData.value = true
					emit(it)
				}
			}
		}
	
	init {
		getListRandomCards()
	}
	
	fun getListRandomCards() {
		if ((networkManager.value == false) && loading.isActive) return
		loading = viewModelScope.launch {
			isLoadingLiveData.value = false
			service.getListRandomCards()?.let {
				mainListLiveData.value = (mainListLiveData.value!!.plus(it))
			}
			isLoadingLiveData.value = true
		}
	}
	
	fun onClickCard(card: Card) {
		clickedCard.value = card
	}
	
	override fun onCleared() {
		CardProvider.isInit = false
		super.onCleared()
	}
	
}