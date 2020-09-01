package com.android.walletforest.TransactionsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.walletforest.TransactionsFragment.TabInfoUtils
import com.android.walletforest.enums.TimeRange
import kotlinx.coroutines.async

class TransactionsFragViewModel(val repository: Repository) : ViewModel() {
    private var currentWallet: Wallet? = null
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    private val tabInfoUtils = TabInfoUtils()
    private val timeRange = TimeRange.MONTH

    val tabInfoList: LiveData<List<TabInfo>> = _tabInfoList

    init {
        viewModelScope.launch {
            repository.getFirstWallet().collect {
                currentWallet = it
                getTabInfoList()
            }

            repository.getNewestTransactionDate(currentWallet?.id!!).collect {
                startTime = it
                getTabInfoList()
            }

            repository.getOldestTransactionDate(currentWallet?.id!!).collect {
                endTime = it
                getTabInfoList()
            }
        }
    }

    private fun getTabInfoList() {
        if (startTime == null || endTime == null || currentWallet == null)
            return

        tabInfoUtils.setProperties(startTime!!, endTime!!, timeRange, currentWallet?.id!!)

        viewModelScope.launch {
            val result = async {
                tabInfoUtils.getTabInfoList()
            }
            _tabInfoList.value = result.await()
        }
    }

}