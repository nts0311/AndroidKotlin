package com.android.walletforest.budget_list_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.databinding.ItemBudgetBinding
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.Repository

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

    fun bind(budget: Budget, budgetClickListener: (Budget) -> Unit) {
        binding.root.setOnClickListener {
            budgetClickListener.invoke(budget)
        }

        val category = categoryMap[budget.categoryId]

        binding.categoryImage.setImageResource(category!!.imageId)
        binding.categoryName.text = category.name
        binding.totalAmount.text = budget.amount.toString()
        binding.amountLeft.text = (budget.amount - budget.spent).toString()
        binding.progressBar.progress = (budget.spent / budget.amount * 100).toInt()
    }

    companion object {
        fun from(parent: ViewGroup): BudgetViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemBudgetBinding.inflate(inflater, parent, false)
            return BudgetViewHolder(binding)
        }
    }
}