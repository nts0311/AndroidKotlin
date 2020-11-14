package com.android.walletforest.TransactionListFragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.pie_chart_detail_activity.FilteringParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionListFragViewModel(val repo: Repository) : ViewModel() {

    val viewMode = repo.viewMode

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    var currentWallet = repo.currentWallet
    var timeRange = TimeRange.MONTH
    var currentViewMode = ViewType.TRANSACTION
    private var dataGrouper = DataGrouper()

    private var groupDataJob: Job? = null

    private var _dataItemList: MutableLiveData<List<DataItem>> = MutableLiveData()
    var dataItemList: LiveData<List<DataItem>> = _dataItemList

    var previousWalletId = -1L

    var filteredList = flow<List<Transaction>> { }

    fun switchViewMode(viewType: ViewType) {
        if (currentViewMode==viewType) return

        currentViewMode = viewType

        groupData()
    }

    override fun onCleared() {
        super.onCleared()
        groupDataJob?.cancel()
    }

    fun setTimeRange(
        start: Long,
        end: Long,
        range: String,
        walletId: Long,
        filteringParams: FilteringParams? = null
    ) {

        if (startTime==start
            && endTime==end
            && range==timeRange.value
            && walletId==previousWalletId
        )
            return

        startTime = start
        endTime = end
        timeRange = TimeRange.valueOf(range)

        val transactionsFlow = repo.getTransactionsBetweenRange(start, end, walletId)

        filteredList = if (filteringParams==null) transactionsFlow
        else {
            if (filteringParams.categoryIdToFilter==-1L)
                transactionsFlow.map { it.filter { transaction -> transaction.type==filteringParams.transactionType } }
            else
                transactionsFlow.map {
                    //include transactions with sub category in parent category
                    val subCategoryId = mutableListOf<Long>()
                    subCategoryId.add(filteringParams.categoryIdToFilter)

                    if (!filteringParams.excludeSubCate)
                        repo.categoryMap.values.forEach { category ->
                            if (category.parentId==filteringParams.categoryIdToFilter)
                                subCategoryId.add(category.id)
                        }
                    it.filter { transaction ->
                        subCategoryId.contains(transaction.categoryId)
                                && transaction.type==filteringParams.transactionType
                    }
                }
        }
        groupData()

        previousWalletId = walletId
    }

    private fun groupData() {
        groupDataJob?.cancel()

        groupDataJob = filteredList.map {
            dataGrouper.doGrouping(it, timeRange, currentViewMode)
        }
            .flowOn(Dispatchers.Default)
            .onEach {
                _dataItemList.value = it
            }
            .launchIn(viewModelScope)
    }
}