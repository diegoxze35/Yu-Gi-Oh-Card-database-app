package com.android.yugioh.ui.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import com.android.yugioh.domain.GetAllArchetypesUseCase
import com.android.yugioh.domain.GetRandomCardsUseCase
import com.android.yugioh.domain.SearchCardByNameUseCase
import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
	val networkManager: NetworkConnectivity,
	private val getRandomCardsUseCase: GetRandomCardsUseCase,
	private val searchCardByNameUseCase: SearchCardByNameUseCase
) : ViewModel() {

	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	private var isSubmit = false
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()
	private val cardData: MutableMap<Card, Drawable> = mutableMapOf()
	private val mainListLiveData: MutableLiveData<List<Card>> = MutableLiveData(emptyList())
	val mainList: LiveData<List<Card>> get() = mainListLiveData

	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard
	private val currentQueryLiveData: MutableLiveData<String> = MutableLiveData()
	val canAddFilterList: Boolean
		get() {
			return currentQueryLiveData.value.orEmpty().isNotEmpty()
		}
	private val isLoadingGoneLiveData: MutableLiveData<Boolean> = MutableLiveData(true)
	val isLoadingGone: LiveData<Boolean> get() = isLoadingGoneLiveData

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
				isLoadingGoneLiveData.value = false
				searchData[query]?.let {
					isLoadingGoneLiveData.value = true
					emit(it)
					return@liveData
				}
				if (networkManager.value == false) return@liveData
				searchCardByNameUseCase(query).also { result ->
					when (result) {
						is Result.Success -> {
							isSearchingLiveData.value = isSearchingLiveData.value?.copy(
								hideSearchMessage = result.body.isNotEmpty(),
								searchNotResult = result.body.isEmpty().also {
									if (it) searchData[query] = result.body
								}
							)
							emit(result.body)
						}
						else -> {/*TODO()*/}
					}
					isLoadingGoneLiveData.value = true
				}
			}
		}

	fun getListRandomCards() {
		if (networkManager.value == false || loading.isActive) return
		loading = viewModelScope.launch {
			isLoadingGoneLiveData.value = false
			when (val cards = getRandomCardsUseCase()) {
				is Result.Success -> {
					mainListLiveData.value = mainListLiveData.value!!.plus(cards.body)
					isLoadingGoneLiveData.value = true
				}
				else -> {/*TODO()*/
				}
			}
		}
	}

	fun onClickCard(card: Card, imageCard: Drawable?) {
		imageCard?.let { cardData[card] = it }
		clickedCard.value = card
	}

	fun getImageCurrentCard(card: Card): Drawable? = cardData[card]

}