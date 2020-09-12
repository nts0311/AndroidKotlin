package com.android.walletforest.add_transaction_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.TransactionsFragment.TabInfoUtils
import com.android.walletforest.databinding.ActivityTransactionDetailBinding
import com.android.walletforest.dateToString
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Repository
import com.android.walletforest.select_category_activity.RESULT_CATEGORY_ID
import com.android.walletforest.select_category_activity.SelectCategoryActivity
import com.android.walletforest.toLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionDetailActivity : AppCompatActivity() {

    companion object {
        val TRANSACTION_ID_PARAM = "transaction_id"
        val WALLET_ID_PARAM = "wallet_id"
    }

    private val LAUNCH_CATEGORY_SELECT_ACTIVITY = 1

    private lateinit var binding: ActivityTransactionDetailBinding
    private lateinit var viewModel: TransactionDetailViewModel
    private var transactionId = -1L
    private var walletId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_detail)
        setSupportActionBar(binding.addTranToolbar)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(applicationContext))

        viewModel = ViewModelProvider(this, vmFactory)
            .get(TransactionDetailViewModel::class.java)

        getArgs()
        registerObservers()
        registerClickListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LAUNCH_CATEGORY_SELECT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val categoryId = data?.getLongExtra(RESULT_CATEGORY_ID, 1)
                setCategory(categoryId!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_transaction_frag_menu, menu)
        return true
    }

    private fun registerClickListener() {

        //select category
        val clickListener: (View) -> Unit = {
            val selectCategory = Intent(this, SelectCategoryActivity::class.java)
            startActivityForResult(selectCategory, LAUNCH_CATEGORY_SELECT_ACTIVITY)
        }
        binding.categoryImg.setOnClickListener(clickListener)
        binding.categoryTxt.setOnClickListener(clickListener)
    }

    private fun getArgs() {
        transactionId = intent.getLongExtra(TRANSACTION_ID_PARAM, -1)
        walletId = intent.getLongExtra(WALLET_ID_PARAM, -1)

        if (transactionId != -1L)
            viewModel.setTransactionId(transactionId)
    }

    private fun setCategory(categoryId: Long) {
        val category = viewModel.categories[categoryId]
        binding.categoryImg.setImageResource(category!!.imageId)
        binding.categoryTxt.text = category.name
    }

    fun dateToFullString(ld: LocalDate): String =
        DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy").format(ld)

    private fun registerObservers() {
        if (transactionId != -1L) {
            viewModel.transaction.observe(this)
            {
                if (it == null) return@observe

                //category
                setCategory(it.categoryId)

                //amount text
                binding.amountTxt.setText(it.amount.toString())
                val color = if (it.type == Constants.TYPE_EXPENSE)
                    ContextCompat.getColor(binding.root.context, R.color.expense_text)
                else
                    ContextCompat.getColor(binding.root.context, R.color.income_text)
                binding.amountTxt.setTextColor(color)

                //date
                binding.dateTxt.text = dateToFullString(toLocalDate(it.time))

                //wallet name
                binding.walletNameTxt.text = viewModel.wallets[it.walletId]?.name

                //note
                binding.noteEdt.setText(it.note)
            }
        }
    }
}