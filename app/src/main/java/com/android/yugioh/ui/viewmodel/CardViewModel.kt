package com.android.yugioh.ui.viewmodel

import android.graphics.drawable.Drawable
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
import kotlinx.coroutines.cancelChildren
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
	private val imageData: MutableMap<Card, Drawable> = mutableMapOf()
	private val mainListLiveData: MutableLiveData<List<Card>> = MutableLiveData(emptyList())
	val mainList: LiveData<List<Card>> get() = mainListLiveData
	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard
	private val currentQueryLiveData: MutableLiveData<String> = MutableLiveData()
	val canAddFilterList: Boolean
		get() {
			return currentQueryLiveData.value.orEmpty().isNotEmpty()
		}
	private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData(true)
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
				if (loading.isActive) return@liveData
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
				if (networkManager.value == false) return@liveData
				try {
					service.searchCard(query).also {
						isSearchingLiveData.value = isSearchingLiveData.value?.copy(
							hideSearchMessage = it.isNotEmpty(),
							searchNotResult = it.isEmpty()
						)
						if (it.isNotEmpty()) searchData[query] = it
						emit(it)
					}
				} catch (e: Exception) {
					loading.cancelChildren()
					return@liveData
				} finally {
					isLoadingLiveData.value = true
				}
			}
		}
	
	fun getListRandomCards() {
		if ((networkManager.value == false) || loading.isActive) return
		loading = viewModelScope.launch {
			isLoadingLiveData.value = false
			try {
				service.getListRandomCards()?.let {
					mainListLiveData.value = (mainListLiveData.value!!.plus(it))
				}
			} catch (e: Exception) {
				loading.cancelChildren()
				return@launch
			} finally {
				isLoadingLiveData.value = true
			}
		}
	}
	
	fun onClickCard(card: Card, imageCard: Drawable?) {
		imageCard?.let { imageData[card] = it }
		clickedCard.value = card
	}
	
	fun getImageCurrentCard(card: Card): Drawable? = imageData[card]
	
	override fun onCleared() {
		CardProvider.isInit = false
		super.onCleared()
	}
	
}