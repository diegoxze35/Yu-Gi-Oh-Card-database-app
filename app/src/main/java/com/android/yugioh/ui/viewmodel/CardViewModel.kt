package com.android.yugioh.ui.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.MutableLiveData
import com.android.yugioh.domain.Searchable
import com.android.yugioh.domain.GetRandomCardsOnlineUseCase
import com.android.yugioh.domain.UseCaseSearchBy
import com.android.yugioh.domain.UseCaseOnlineSearchBy
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
	private val getRandomCardsUseCase: GetRandomCardsOnlineUseCase
) : ViewModel() {

	private var loading =
		CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY, block = {})
	private val cardData: MutableMap<Card, Drawable> = mutableMapOf()
	private val clickedCard: MutableLiveData<Card> = MutableLiveData()
	val currentCard: LiveData<Card> get() = clickedCard

	private val _fragmentListLiveData = MutableLiveData(
		ListFragmentState(
			SearchingState(),
			LoadListState(mainList = emptyList(), isLoadingGone = false)
		)
	)
	val fragmentListLiveData: LiveData<ListFragmentState> get() = _fragmentListLiveData
	private val useCaseLiveData: MutableLiveData<UseCaseSearchBy<*>> = MutableLiveData()

	val canAddFilterList: Boolean
		get() {
			return searchable.query.orEmpty().isNotEmpty()
		}
	private var searchable = Searchable(query = null, options = null)
	fun setSearchUseCase(useCase: UseCaseSearchBy<*>, query: Searchable) {
		searchable = query
		useCaseLiveData.value = useCase
	}

	val filterListLiveData: LiveData<List<Card>> =
		Transformations.switchMap(useCaseLiveData) { useCase ->
			liveData<List<Card>> {
				_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
					copy(loadListState = loadListState.copy(isLoadingGone = useCase !is UseCaseOnlineSearchBy))
				}
				when (val resultList = useCase(searchable)) {
					is Result.Success -> {
						_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
							copy(
								searchingState = searchingState.copy(
									hideSearchMessage = resultList.body.isNotEmpty(),
									searchNotResult = useCase is UseCaseOnlineSearchBy
											&& resultList.body.isEmpty()
								)
							)
						}
						emit(resultList.body)
					}
					is Result.Error -> {

					}
				}
				_fragmentListLiveData.value = with(_fragmentListLiveData.value!!) {
					copy(loadListState = loadListState.copy(isLoadingGone = true))
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
				is Result.Error -> {/*TODO()*/
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