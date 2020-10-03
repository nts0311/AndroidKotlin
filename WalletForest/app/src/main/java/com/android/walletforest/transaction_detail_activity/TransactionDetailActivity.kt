package com.android.walletforest.transaction_detail_activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.*
import com.android.walletforest.databinding.ActivityTransactionDetailBinding
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Repository
import com.android.walletforest.select_category_activity.RESULT_CATEGORY_ID
import com.android.walletforest.select_category_activity.SelectCategoryActivity
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
    private var currentTransaction: Transaction? = null
    private var transactionDate: Long = 0L
    private var transactionCategoryId = -1L


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

        //set new category to the current transaction which user have choice
        if (requestCode == LAUNCH_CATEGORY_SELECT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val categoryId = data?.getLongExtra(RESULT_CATEGORY_ID, 1)
                transactionCategoryId = categoryId!!
                setCategory(categoryId)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_transaction_frag_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_save_transaction -> {
                saveItemClick()
                true
            }

            R.id.item_delete_transaction -> {
                deleteItemClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveItemClick() {

        if (binding.amountTxt.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_amount_reminder), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (transactionCategoryId == -1L) {
            Toast.makeText(this, getString(R.string.enter_cate_reminder), Toast.LENGTH_SHORT)
                .show()
            return
        }

        //save transaction if edited
        if (transactionId != -1L) {

            val newTransaction = currentTransaction?.copy()

            newTransaction?.apply {
                categoryId = transactionCategoryId
                amount = binding.amountTxt.text.toString().toLong()
                note = binding.noteEdt.text.toString()
                date = transactionDate
                type = viewModel.categories[transactionCategoryId]?.type!!
            }

            viewModel.updateTransaction(newTransaction!!, currentTransaction?.amount!!)

        }
        //create a new transaction
        else {
            val newTransaction = Transaction(
                0,
                transactionCategoryId,
                walletId,
                viewModel.categories[transactionCategoryId]?.type!!,
                binding.amountTxt.text.toString().toLong(),
                binding.noteEdt.text.toString(),
                transactionDate
            )
            viewModel.insertTransaction(newTransaction)
        }

        finish()
    }

    private fun deleteItemClick() {
        if (currentTransaction == null) return

        viewModel.deleteTransaction(currentTransaction!!)
        finish()
    }

    private fun registerClickListener() {

        //select category
        val clickListener: (View) -> Unit = {
            val selectCategory = Intent(this, SelectCategoryActivity::class.java)
            startActivityForResult(selectCategory, LAUNCH_CATEGORY_SELECT_ACTIVITY)
        }
        binding.categoryTxt.setOnClickListener(clickListener)

        //date picker
        val dateSetListener: (DatePicker, Int, Int, Int) -> Unit =
            { _, year, monthOfYear, dayOfMonth ->
                val ld = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                binding.dateTxt.text = dateToFullString(ld)
                transactionDate = toEpoch(ld)
            }

        binding.dateTxt.setOnClickListener {

            val ld = toLocalDate(transactionDate)

            DatePickerDialog(
                this, dateSetListener,
                ld.year, ld.monthValue - 1, ld.dayOfMonth
            ).show()
        }
    }

    private fun getArgs() {
        transactionId = intent.getLongExtra(TRANSACTION_ID_PARAM, -1)
        walletId = intent.getLongExtra(WALLET_ID_PARAM, -1)

        if (transactionId != -1L)
            viewModel.setTransactionId(transactionId)

        if (walletId != -1L) {
            binding.walletNameTxt.text = viewModel.wallets[walletId]?.name
            binding.dateTxt.text = dateToFullString(LocalDate.now())
            transactionDate = toEpoch(LocalDate.now())
        }

    }

    private fun setCategory(categoryId: Long) {
        val category = viewModel.categories[categoryId]
        binding.categoryImg.setImageResource(category!!.imageId)
        binding.categoryTxt.text = category.name
    }

    private fun dateToFullString(ld: LocalDate): String =
        DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy").format(ld)

    private fun registerObservers() {
        if (transactionId != -1L) {
            viewModel.transaction.observe(this)
            {
                if (it == null) return@observe

                currentTransaction = it
                transactionDate = it.date
                transactionCategoryId = it.categoryId

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
                binding.dateTxt.text = dateToFullString(toLocalDate(it.date))

                //wallet name
                binding.walletNameTxt.text = viewModel.wallets[it.walletId]?.name

                //note
                binding.noteEdt.setText(it.note)
            }
        }
    }
}