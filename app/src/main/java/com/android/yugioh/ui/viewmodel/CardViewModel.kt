package com.android.yugioh.ui.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.MutableLiveData
import com.android.yugioh.domain.GetRandomCardsUseCase
import com.android.yugioh.domain.SearchCardByNameUseCase
import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import com.android.yugioh.ui.ListFragmentState
import com.android.yugioh.ui.LoadListState
import com.android.yugioh.ui.SearchingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
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
	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard
	private val currentQueryLiveData: MutableLiveData<String> = MutableLiveData()
	val canAddFilterList: Boolean
		get() {
			return currentQueryLiveData.value.orEmpty().isNotEmpty()
		}

	private val _fragmentListLiveData = MutableLiveData(
		ListFragmentState(
			SearchingState(),
			LoadListState(mainList = emptyList(), isLoadingGone = false)
		)
	)
	val fragmentListLiveData: LiveData<ListFragmentState> get() = _fragmentListLiveData

	fun setQuerySearch(query: String, isSubmit: Boolean) {
		if (loading.isActive) return
		if (query.isEmpty()) {
			_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
				copy(
					searchingState = searchingState.copy(
						hideSearchMessage = true,
						searchNotResult = false
					),
					loadListState = loadListState.copy(mainList = loadListState.mainList)
				)
			}
		}
		this.isSubmit = isSubmit
		currentQueryLiveData.value = query
	}

	val filterListLiveData: LiveData<List<Card>> =
		Transformations.switchMap(currentQueryLiveData) { query ->
			liveData<List<Card>> {
				searchData[query]?.let { emit(it); return@liveData }
				if (!isSubmit) {
					_fragmentListLiveData.value!!.loadListState.mainList.filter {
						it.name.contains(query, ignoreCase = true)
					}.also {
						_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
							copy(
								searchingState = searchingState.copy(
									hideSearchMessage = it.isNotEmpty(),
									searchNotResult = false
								)
							)
						}
						emit(it)
						return@liveData
					}
				}
				if (networkManager.value == false) return@liveData
				_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
					copy(loadListState = loadListState.copy(isLoadingGone = false))
				}
				searchCardByNameUseCase(query).run {
					when (this) {
						is Result.Success -> {
							_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
								copy(
									searchingState = searchingState.copy(
										hideSearchMessage = body.isNotEmpty(),
										searchNotResult = body.isEmpty().also {
											if (it) searchData[query] = body
										}
									)
								)
							}
							emit(body)
						}
						else -> {/*TODO()*/
						}
					}
					_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
						copy(loadListState = loadListState.copy(isLoadingGone = true))
					}
				}
			}
		}

	fun getListRandomCards() {
		if (networkManager.value == false || loading.isActive) return
		loading = viewModelScope.launch {
			_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
				copy(loadListState = loadListState.copy(isLoadingGone = false))
			}
			when (val cards = getRandomCardsUseCase()) {
				is Result.Success -> {
					_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
						copy(
							loadListState = loadListState.copy(
								isLoadingGone = true,
								mainList = loadListState.mainList.plus(cards.body)
							),
						)
					}
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