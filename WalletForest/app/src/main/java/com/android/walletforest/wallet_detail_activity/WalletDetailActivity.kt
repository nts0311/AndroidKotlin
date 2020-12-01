package com.android.walletforest.wallet_detail_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.select_image_activity.RESULT_ICON_ID
import com.android.walletforest.select_image_activity.SelectImageActivity
import kotlinx.android.synthetic.main.activity_wallet_detail.*
import java.lang.NumberFormatException

class WalletDetailActivity : AppCompatActivity() {
    companion object {
        val WALLET_ID_PARAM = "wallet_id"
    }

    private val LAUNCH_ICON_SELECT_ACTIVITY = 1

    private var walletId = -1L
    private var iconId = R.drawable.icon
    private lateinit var viewModel: WalletDetailActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_detail)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(applicationContext))
        viewModel = ViewModelProvider(this, vmFactory)
            .get(WalletDetailActivityViewModel::class.java)

        getArgs()

        if (walletId!=-1L) {
            deleteWalletBtn.visibility = View.VISIBLE
            viewModel.setCurrentWallet(walletId)
        } else {
            wallet_icon_img.setImageResource(iconId)
        }

        registerClickListener()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==LAUNCH_ICON_SELECT_ACTIVITY) {
            if (resultCode==RESULT_OK) {
                iconId = data?.getIntExtra(RESULT_ICON_ID, iconId)!!
                wallet_icon_img.setImageResource(iconId)
            }
        }
    }

    private fun getArgs() {
        walletId = intent.getLongExtra(WALLET_ID_PARAM, -1)
    }

    private fun setupToolbar() {
        wallet_detail_toolbar.title =
            if (walletId==-1L) getString(R.string.add_wallet)
            else getString(R.string.edit_wallet)
        setSupportActionBar(wallet_detail_toolbar)
    }

    private fun registerObservers() {
        viewModel.wallet.observe(this) {

            if (it==null) return@observe

            wallet_name_edt.setText(it.name)
            wallet_currency_edt.setText(it.currency)
            wallet_icon_img.setImageResource(it.imageId)
        }
    }

    private fun registerClickListener() {
        deleteWalletBtn.setOnClickListener {
            deleteCurrentWallet()
        }

        wallet_icon_img.setOnClickListener {
            val selectIcon = Intent(this@WalletDetailActivity, SelectImageActivity::class.java)
            startActivityForResult(selectIcon, LAUNCH_ICON_SELECT_ACTIVITY)
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


        val newWallet = Wallet(
            0,
            wallet_name_edt.text.toString(),
            iconId,
            0L,
            wallet_currency_edt.text.toString()
        )

        if (walletId==-1L) {
            viewModel.addWallet(newWallet)
        } else {
            newWallet.id = walletId
            viewModel.updateWallet(newWallet)
        }

        finish()
    }

    private fun deleteCurrentWallet() {
        if (walletId==-1L) return

        AlertDialog.Builder(this).run {

            setMessage(R.string.delete_wallet_message)
            setTitle(getString(R.string.delete_wallet_dialog_title, viewModel.wallet.value!!.name))

            setPositiveButton(R.string.ok) { _, _ ->
                viewModel.deleteCurrentWallet()
                finish()
            }

            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            create()
        }.show()
    }
}