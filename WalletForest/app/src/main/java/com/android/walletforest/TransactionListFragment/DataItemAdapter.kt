package com.android.walletforest.TransactionListFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R
import com.android.walletforest.TransactionsFragment.TabInfoUtils
import com.android.walletforest.databinding.ItemDividerBinding
import com.android.walletforest.databinding.ItemTransactionBinding
import com.android.walletforest.enums.Constants
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import java.time.format.DateTimeFormatter

class DataItemDiffCallbacks : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.itemType == newItem.itemType
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

private const val ITEM_HEADER = 0
private const val ITEM_DIVIDER = 1
private const val ITEM_TRANSACTION = 2


class DataItemAdapter(
    viewMode: ViewType, timeRange: TimeRange,
    var categoriesMap: Map<Long, Category>
) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DataItemDiffCallbacks()) {
    var viewMode = viewMode
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var timeRange = timeRange
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var itemClickListener: (transaction: Transaction) -> Unit = {}

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is DataItem.TransactionItem -> ITEM_TRANSACTION
        is DataItem.DividerItem -> ITEM_DIVIDER
        is DataItem.HeaderItem -> ITEM_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ITEM_TRANSACTION -> TransactionItemViewHolder.from(parent)
            ITEM_DIVIDER -> DividerItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionItemViewHolder -> {
                val transaction = (getItem(position) as DataItem.TransactionItem).transaction
                holder.bind(transaction, viewMode, categoriesMap[transaction.categoryId]!!, itemClickListener)
            }

            is DividerItemViewHolder -> {
                val dividerItem = getItem(position) as DataItem.DividerItem
                holder.bind(
                    dividerItem,
                    viewMode,
                    timeRange,
                    categoriesMap[dividerItem.categoryId]!!
                )
            }
        }
    }
}


class TransactionItemViewHolder(var binding: ItemTransactionBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(
        transaction: Transaction,
        viewMode: ViewType,
        category: Category,
        itemClickListener: (transaction: Transaction) -> Unit
    ) {

        binding.root.setOnClickListener { itemClickListener(transaction) }

        val color = if (transaction.type == Constants.TYPE_EXPENSE)
            ContextCompat.getColor(binding.root.context, R.color.expense_text)
        else
            ContextCompat.getColor(binding.root.context, R.color.income_text)

        binding.amountText.setTextColor(color)
        binding.amountText.text = transaction.amount.toString()

        binding.noteText.text = transaction.note

        binding.apply {
            if (viewMode == ViewType.TRANSACTION) {
                dayOfMonthText.visibility = View.INVISIBLE
                dateText.visibility = View.INVISIBLE

                categoryImage.setImageResource(category.imageId)
                categoryImage.visibility = View.VISIBLE

                categoryText.text = category.name
                categoryText.visibility = View.VISIBLE

            } else {
                categoryImage.visibility = View.INVISIBLE
                categoryText.visibility = View.INVISIBLE

                val date = TabInfoUtils.toLocalDate(transaction.time)
                val formatter = DateTimeFormatter.ofPattern("MMMM yyyy, EEEE")

                dateText.text = formatter.format(date)
                dateText.visibility = View.VISIBLE

                dayOfMonthText.text = date.dayOfMonth.toString()
                dayOfMonthText.visibility = View.VISIBLE

            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): TransactionItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTransactionBinding.inflate(inflater, parent, false)
            binding.root.setOnClickListener {

            }
            return TransactionItemViewHolder(binding)
        }
    }
}

class DividerItemViewHolder(var binding: ItemDividerBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        dividerItem: DataItem.DividerItem,
        viewMode: ViewType,
        timeRange: TimeRange,
        category: Category
    ) {
        binding.amountText.text = dividerItem.totalAmount.toString()

        if (viewMode == ViewType.TRANSACTION) {
            bindWithTime(dividerItem, timeRange)
        } else
            bindWithCategory(dividerItem, category)
    }

    private fun bindWithTime(
        dividerItem: DataItem.DividerItem,
        timeRange: TimeRange
    ) {
        binding.apply {
            categoryImage.visibility = View.INVISIBLE
            categoryText.visibility = View.INVISIBLE
            numOfTransactionText.visibility = View.INVISIBLE

            dayOrMonthText.visibility = View.VISIBLE
            dayOfWeekText.visibility = View.VISIBLE
            monthYearText.visibility = View.VISIBLE
        }

        if (timeRange == TimeRange.MONTH || timeRange == TimeRange.WEEK || timeRange == TimeRange.CUSTOM) {

            binding.dayOrMonthText.text = dividerItem.date.dayOfMonth.toString()

            var formatter = DateTimeFormatter.ofPattern("EEEE")
            binding.dayOfWeekText.text = formatter.format(dividerItem.date)

            formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            binding.monthYearText.text = formatter.format(dividerItem.date)

        } else if (timeRange == TimeRange.YEAR) {
            binding.dayOfWeekText.visibility = View.INVISIBLE

            val formatter = DateTimeFormatter.ofPattern("MMM")
            binding.dayOrMonthText.text = formatter.format(dividerItem.date)

            binding.monthYearText.text = dividerItem.date.year.toString()
        }
    }

    private fun bindWithCategory(
        dividerItem: DataItem.DividerItem,
        category: Category
    ) {
        binding.apply {
            categoryImage.visibility = View.VISIBLE
            categoryText.visibility = View.VISIBLE
            numOfTransactionText.visibility = View.VISIBLE

            dayOrMonthText.visibility = View.INVISIBLE
            dayOfWeekText.visibility = View.INVISIBLE
            monthYearText.visibility = View.INVISIBLE

            categoryImage.setImageResource(category.imageId)
            categoryText.text = category.name
            numOfTransactionText.text = dividerItem.numOfTransactions.toString()
        }
    }

    companion object {
        fun from(parent: ViewGroup): DividerItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemDividerBinding.inflate(inflater, parent, false)
            return DividerItemViewHolder(binding)
        }
    }
}























