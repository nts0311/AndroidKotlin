package com.android.walletforest.wallet_detail_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.android.synthetic.main.activity_wallet_detail.*
import java.lang.NumberFormatException

class WalletDetailActivity : AppCompatActivity() {
    companion object {
        val WALLET_ID_PARAM = "wallet_id"
    }

    private var walletId = -1L
    private lateinit var viewModel: WalletDetailActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_detail)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(applicationContext))
        viewModel = ViewModelProvider(this, vmFactory)
            .get(WalletDetailActivityViewModel::class.java)

        getArgs()

        if (walletId != -1L)
            viewModel.setCurrentWallet(walletId)

        registerObservers()

        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.wallet_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_save_wallet -> {
                saveWallet()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getArgs() {
        walletId = intent.getLongExtra(WALLET_ID_PARAM, -1)
    }

    private fun setupToolbar() {
        wallet_detail_toolbar.title =
            if (walletId == -1L) getString(R.string.add_wallet)
            else getString(R.string.edit_wallet)
        setSupportActionBar(wallet_detail_toolbar)
    }

    private fun registerObservers() {
        viewModel.wallet.observe(this){
            wallet_name_edt.setText(it.name)
            wallet_balance_edt.setText(it.amount.toString())
        }
    }

    private fun saveWallet() {
        if (wallet_name_edt.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_name_reminder), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (wallet_currency_edt.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_currency_reminder), Toast.LENGTH_SHORT)
                .show()
            return
        }

        //check amount is valid number
        balance_input_layout.error = null
        try {
            wallet_balance_edt.text.toString().toLong()
        } catch (e: NumberFormatException) {
            balance_input_layout.error = getString(R.string.error_invalid_amount)
            return
        }


        if (walletId == -1L) {
            val newWallet = Wallet(
                0,
                wallet_name_edt.text.toString(),
                R.drawable.icon,
                wallet_balance_edt.text.toString().toLong()
            )

            viewModel.addWallet(newWallet)
        } else {

        }
    }
}