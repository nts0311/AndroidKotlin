package com.android.walletforest.budget_list_fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.NumberFormatter
import com.android.walletforest.R
import com.android.walletforest.databinding.ItemBudgetBinding
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.Repository
import java.util.*

class BudgetAdapter : RecyclerView.Adapter<BudgetViewHolder>() {

    var budgetList: List<Budget> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var budgetClickListener: (Budget) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder =
        BudgetViewHolder.from(parent)

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgetList[position], budgetClickListener)
    }

    override fun getItemCount(): Int = budgetList.size
}

class BudgetViewHolder(private val binding: ItemBudgetBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val categoryMap =
        Repository.getInstance(binding.root.context.applicationContext).categoryMap

    @SuppressLint("SetTextI18n")
    fun bind(budget: Budget, budgetClickListener: (Budget) -> Unit) {
        binding.root.setOnClickListener {
            budgetClickListener.invoke(budget)
        }

        val category = categoryMap[budget.categoryId]

        binding.apply {
            categoryImage.setImageResource(category!!.imageId)
            categoryName.text = category.name

            totalAmount.text = NumberFormatter.format(budget.amount)

            val diffText = if(budget.amount > budget.spent)
                root.context.getString(R.string.left)
            else
                root.context.getString(R.string.overspent)

            val diffAmount = if(budget.amount > budget.spent) budget.amount - budget.spent
            else budget.spent - budget.amount

            amountLeft.text = "$diffText ${NumberFormatter.format(diffAmount)}"

            setProgressProgressBar(progressBar, (budget.spent.toFloat() / budget.amount * 100).toInt())
        }
    }

    private fun setProgressProgressBar(progressBar: ProgressBar, progress: Int) {
        progressBar.progress = progress
        progressBar.scaleY = 1.5f
        progressBar.progressTintList = when (progress) {
            in 0..69 -> ColorStateList.valueOf(Color.rgb(0,153,51))
            in 70..89 -> ColorStateList.valueOf(Color.rgb(255, 153, 51))
            else -> ColorStateList.valueOf(Color.RED)
        }
    }

    companion object {
        fun from(parent: ViewGroup): BudgetViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemBudgetBinding.inflate(inflater, parent, false)
            return BudgetViewHolder(binding)
        }
    }
}