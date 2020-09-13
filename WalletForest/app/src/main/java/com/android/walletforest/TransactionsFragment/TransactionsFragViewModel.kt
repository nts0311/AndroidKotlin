package com.android.walletforest.TransactionsFragment

import android.util.Log
import androidx.lifecycle.*
import com.android.walletforest.TransactionListFragment.DataGrouper
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.toEpoch
import com.android.walletforest.toLocalDate
import kotlinx.coroutines.async
import java.time.LocalDate

class TransactionsFragViewModel(private val repository: Repository) : ViewModel() {

    var tabLayoutPos = -1

    var tabInfoList = repository.tabInfoList
    var timeRange = repository.timeRange
}