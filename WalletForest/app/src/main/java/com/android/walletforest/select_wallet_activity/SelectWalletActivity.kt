package com.android.walletforest.select_wallet_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.wallet_detail_activity.WalletDetailActivity
import kotlinx.android.synthetic.main.activity_select_wallet.*

const val RESULT_WALLET_ID = "result_wallet_id"

class SelectWalletActivity : AppCompatActivity() {

    private lateinit var viewModel: SelectWalletActivityViewModel
    private val walletAdapter = WalletAdapter()
    private var isEditingWallets = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_wallet)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(applicationContext))
        viewModel = ViewModelProvider(this, vmFactory)
            .get(SelectWalletActivityViewModel::class.java)

        setSupportActionBar(select_wallet_toolbar)

        setupRecycleView()
        registerListener()

        add_wallet_fab.setOnClickListener {
            val addWalletActivity =
                Intent(this@SelectWalletActivity, WalletDetailActivity::class.java)
            startActivity(addWalletActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_wallet_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_edit_wallet -> {
                isEditingWallets = !isEditingWallets

                item.title = if (isEditingWallets) getString(R.string.done)
                else getString(R.string.edit)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun registerListener() {
        viewModel.walletList.observe(this)
        {
            if (it != null) {
                //viewModel.updateWalletMap(it)
                walletAdapter.walletList = it
            }
        }
    }

    private fun setupRecycleView() {
        walletAdapter.itemClickListener = clickListener@{

            if (!isEditingWallets) {
                /*val result = Intent()
                result.putExtra(RESULT_WALLET_ID, it.id)
                setResult(RESULT_OK, result)*/

                viewModel.selectWallet(it.id)
                finish()
            } else {
                if (it.id == 1L)
                    return@clickListener

                val editWallet = Intent(this@SelectWalletActivity, WalletDetailActivity::class.java)
                editWallet.putExtra(WalletDetailActivity.WALLET_ID_PARAM, it.id)
                startActivity(editWallet)
            }
        }

        wallet_list_rv.adapter = walletAdapter
        wallet_list_rv.layoutManager = LinearLayoutManager(this)
        wallet_list_rv.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}