package com.android.walletforest.add_budget_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.*
import com.android.walletforest.databinding.ActivityAddBudgetBinding
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.select_budget_range.BudgetRange
import com.android.walletforest.select_budget_range.SelectBudgetRangeActivity
import com.android.walletforest.select_category_activity.RESULT_CATEGORY_ID
import com.android.walletforest.select_category_activity.SelectCategoryActivity
import com.android.walletforest.utils.AmountTextWatcher
import com.android.walletforest.utils.NumberFormatter
import com.android.walletforest.utils.toLocalDate
import kotlinx.android.synthetic.main.activity_add_budget.*

class AddBudgetActivity : AppCompatActivity() {

    companion object {
        const val BUDGET_ID = "budgetId"
    }

    private val LAUNCH_CATEGORY_SELECT_ACTIVITY = 1
    private val LAUNCH_SELECT_BUDGET_RANGE_ACTIVITY = 2

    private lateinit var binding: ActivityAddBudgetBinding
    private lateinit var viewModel: AddBudgetViewModel

    private var budgetId = -1L
    private var currentWalletId = -1L

    private var categoryId = -1L
    private var budgetRange = BudgetRange()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_budget)
        setSupportActionBar(binding.addBudgetToolbar)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(applicationContext))
        viewModel = ViewModelProvider(this, vmFactory).get(AddBudgetViewModel::class.java)

        if (intent.hasExtra(BUDGET_ID))
            budgetId = intent.getLongExtra(BUDGET_ID, -1)

        binding.amountTxt.addTextChangedListener(AmountTextWatcher(binding.amountTxt))

        registerObservers()
        registerClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_budget_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.item_save_budget) {
            //save the budget
            saveBudget()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //set new category to the current transaction which user have choice
        if (requestCode == LAUNCH_CATEGORY_SELECT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val categoryId = data?.getLongExtra(RESULT_CATEGORY_ID, 1)
                this.categoryId = categoryId!!
                setCategory(categoryId)
            }
        } else if (requestCode == LAUNCH_SELECT_BUDGET_RANGE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                budgetRange =
                    data?.getSerializableExtra(SelectBudgetRangeActivity.RESULT_BUDGET_RANGE) as BudgetRange
                setRange(budgetRange)
            }
        }
    }

    private fun setCategory(categoryId: Long) {
        val category = viewModel.categoriesMap[categoryId]
        binding.categoryImg.setImageResource(category!!.imageId)
        binding.categoryTxt.text = category.name
    }

    private fun setRange(budgetRange: BudgetRange) {
        range_txt.text = budgetRange.rangeDetail
    }

    private fun registerObservers() {
        viewModel.currentWallet.observe(this)
        {
            currentWalletId = it.id
            wallet_icon.setImageResource(it.imageId)
            wallet_name_txt.text = it.name
        }
    }

    private fun registerClickListeners() {
        //select category
        binding.categoryTxt.setOnClickListener {
            val selectCategory = Intent(this, SelectCategoryActivity::class.java)
            startActivityForResult(selectCategory, LAUNCH_CATEGORY_SELECT_ACTIVITY)
        }

        //select range of the budget
        binding.rangeTxt.setOnClickListener {
            val selectRange = Intent(this, SelectBudgetRangeActivity::class.java)
            startActivityForResult(selectRange, LAUNCH_SELECT_BUDGET_RANGE_ACTIVITY)
        }
    }

    private fun saveBudget() {
        val amount = NumberFormatter.toLong(binding.amountTxt.text.toString())
        //create a new budget
        if (budgetId == -1L) {
            val newBudget = Budget(
                0,
                categoryId,
                currentWalletId,
                amount,
                0,
                budgetRange.startDate,
                budgetRange.endDate
            )
            viewModel.insertBudget(newBudget)
            finish()

        } else {

        }
    }
}