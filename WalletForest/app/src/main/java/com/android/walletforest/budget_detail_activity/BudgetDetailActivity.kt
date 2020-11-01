package com.android.walletforest.budget_detail_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.utils.NumberFormatter
import kotlinx.android.synthetic.main.activity_budget_detail.*
import kotlinx.android.synthetic.main.item_transaction.*

class BudgetDetailActivity : AppCompatActivity() {

    companion object{
        val BUDGET_ID_KEY = "budgetId"
    }



    private lateinit var viewModel: BudgetDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        viewModel = ViewModelProvider(
            this,
            RepoViewModelFactory(Repository.getInstance(applicationContext))
        ).get(BudgetDetailViewModel::class.java)

        viewModel.budgetId = intent.getLongExtra(BUDGET_ID_KEY, 0)
        registerObservers()
    }

    private fun registerObservers()
    {
        viewModel.currentBudget.observe(this)
        {
            if(it==null)
                return@observe

            if(it.categoryId==-1L)
            {
                category_img.setImageResource(R.drawable.ic_category_all)
                category_txt.text = applicationContext.getString(R.string.all_categories)
            }
            else
            {
                val category = viewModel.categoryMap[it.categoryId]!!
                category_img.setImageResource(category.imageId)
                category_txt.text = category.name
            }

            amount_txt.text = NumberFormatter.format(it.amount)
            amount_spent_txt.text = NumberFormatter.format(it.spent)
            amount_left_txt.text = NumberFormatter.format(it.amount - it.spent)

            val wallet = viewModel.walletMap[it.walletId]!!
            wallet_name_txt.text = wallet.name
            wallet_icon.setImageResource(wallet.imageId)
        }
    }
}