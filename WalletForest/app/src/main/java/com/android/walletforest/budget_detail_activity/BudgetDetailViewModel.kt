package com.android.walletforest.budget_detail_activity

import androidx.lifecycle.*
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.utils.toLocalDate
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.time.temporal.ChronoUnit
import kotlin.math.max

class BudgetDetailViewModel(private val repository: Repository) : ViewModel() {

    var budgetId = 0L

    var currentBudget = liveData {
        emitSource(repository.getBudgetById(budgetId).asLiveData())
    }

    var recommendDailySpending = MutableLiveData(0.0f)
    var projectedSpending = MutableLiveData(0.0f)
    var actualDailySpending = MutableLiveData(0.0f)
    var lineEntries = MutableLiveData<Pair<List<Entry>, List<Entry>>>()
    var totalDay = MutableLiveData(0)
    var dayRemaining = MutableLiveData(0)

    var categoryMap = repository.categoryMap
    var walletMap = repository.walletMap


    fun setBudgetInfo(budget: Budget) {

        val totalDay =
            ChronoUnit.DAYS.between(toLocalDate(budget.startDate), toLocalDate(budget.endDate))
                .toInt() + 1
        val dayRemaining = ChronoUnit.DAYS.between(
            toLocalDate(System.currentTimeMillis()),
            toLocalDate(budget.endDate)
        ).toInt() + 1

        this.totalDay.value = totalDay
        this.dayRemaining.value = dayRemaining

        repository.getTransactionsBetweenRange(budget.startDate, budget.endDate, budget.walletId)
            .map {
                val result = it.filter { transaction -> transaction.type==Constants.TYPE_EXPENSE }

                if (budget.categoryId!=-1L)
                    result.filter { transaction ->
                        transaction.categoryId==budget.categoryId
                                || categoryMap[transaction.categoryId]!!.parentId==budget.categoryId
                    }
                else
                    result
            }
            .onEach { transactionList ->
                val transactionMap = transactionList.groupBy { transaction ->
                    ChronoUnit.DAYS.between(
                        toLocalDate(budget.startDate),
                        toLocalDate(transaction.date)
                    ).toInt()
                }

                val dayPassed = ChronoUnit.DAYS.between(
                    toLocalDate(budget.startDate),
                    toLocalDate(System.currentTimeMillis())
                ).toInt()

                var maxIndex = max(transactionMap.keys.maxOrNull() ?: 0, dayPassed)

                val entries1 = List(maxIndex + 1) { i -> Entry(i.toFloat(), 0.0f) }
                var totalSpent = 0L

                for (i in entries1.indices) {
                    if (transactionMap.containsKey(i)) {
                        val sum =
                            transactionMap[i]!!.fold(0L) { acc, transaction -> acc + transaction.amount }
                        entries1[i].y += sum
                        totalSpent += sum
                    }
                    if (i > 0) entries1[i].y += entries1[i - 1].y
                }

                val actualDailySpent = (totalSpent / (totalDay - dayRemaining)).toFloat()

                actualDailySpending.postValue(actualDailySpent)
                recommendDailySpending.postValue(((budget.amount - budget.spent) / dayRemaining).toFloat())
                projectedSpending.postValue((totalSpent + actualDailySpent * dayRemaining))


                //forecasting how much user will spent

                var lastSpent = entries1.last().y

                val entries2 = List(totalDay - maxIndex + 1) { i ->
                    val entry = Entry((maxIndex++).toFloat(), lastSpent)
                    lastSpent += actualDailySpent
                    entry
                }

                val resultPair = Pair(entries1, entries2)

                lineEntries.postValue(resultPair)
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }


    fun deleteBudget(budget: Budget) {
        repository.deleteBudget(budget)
    }
}