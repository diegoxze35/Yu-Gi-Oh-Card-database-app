package com.android.yugioh.ui.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.MutableLiveData
import com.android.yugioh.domain.GetRandomCardsOnlineUseCase
import com.android.yugioh.domain.SearchCardByOptionsOlineUseCase
import com.android.yugioh.domain.UseCaseOnlineSearchBy
import com.android.yugioh.domain.UseCaseSearchBy
import com.android.yugioh.domain.data.Card
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
	private val getRandomCardsUseCase: GetRandomCardsOnlineUseCase
) : ViewModel() {

	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	var querySearch = String()
	private val cardData: MutableMap<Card, Drawable> by lazy { mutableMapOf() }
	lateinit var clickedCard: Card
	private val _fragmentListLiveData = MutableLiveData(
		ListFragmentState(
			SearchingState(),
			LoadListState(mainList = emptyList(), isLoadingGone = false)
		)
	)
	val fragmentListLiveData: LiveData<ListFragmentState> get() = _fragmentListLiveData
	private val useCaseLiveData: MutableLiveData<UseCaseSearchBy<*>> = MutableLiveData()
	val canAddFilterList: Boolean
		get() = querySearch.isNotEmpty() || useCaseLiveData.value is SearchCardByOptionsOlineUseCase
	val isRemoteSearch: Boolean
		get() = useCaseLiveData.value is UseCaseOnlineSearchBy
	val isRemoteSearchAdvance: Boolean
		get() = useCaseLiveData.value is SearchCardByOptionsOlineUseCase

	init {
		getListRandomCards()
	}

	fun getImageCurrentCardOrNull(card: Card): Drawable? = cardData[card]
	fun setSearchUseCase(useCase: UseCaseSearchBy<*>) {
		useCaseLiveData.value = useCase
	}

	fun getListRandomCards() {
		if (loading.isActive) return
		loading = viewModelScope.launch {
			_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
				copy(loadListState = loadListState.copy(isLoadingGone = false))
			}
			getRandomCardsUseCase()
				.onSuccess {
					_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
						copy(
							loadListState = loadListState.copy(
								isLoadingGone = true,
								mainList = loadListState.mainList.plus(it)
							),
						)
					}
				}
				.onFailure { /*TODO()*/ }
		}
	}

	fun onClickCard(card: Card, imageCard: Drawable?) {
		imageCard?.let { cardData[card] = it }
		clickedCard = card
	}

	val filterListLiveData: LiveData<List<Card>> =
		Transformations.switchMap(useCaseLiveData) { useCase ->
			liveData<List<Card>> {
				_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
					copy(loadListState = loadListState.copy(isLoadingGone = useCase !is UseCaseOnlineSearchBy))
				}
				useCase(querySearch)
					.onSuccess {
						_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
							copy(
								searchingState = searchingState.copy(
									hideSearchMessage = it.isNotEmpty(),
									searchNotResult = useCase is UseCaseOnlineSearchBy && it.isEmpty()
								)
							)
						}
						emit(it)
					}
					.onFailure {
						/*TODO()*/
					}
				_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
					copy(loadListState = loadListState.copy(isLoadingGone = true))
				}
			}
		}

}