package com.android.walletforest.select_wallet_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.Repository
import com.android.walletforest.wallet_detail_activity.WalletDetailActivity
import kotlinx.android.synthetic.main.activity_select_wallet.*

const val RESULT_WALLET_ID = "result_wallet_id"

class SelectWalletActivity : AppCompatActivity() {

    private lateinit var viewModel: SelectWalletActivityViewModel
    private val walletAdapter = WalletAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_wallet)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(applicationContext))
        viewModel = ViewModelProvider(this, vmFactory)
            .get(SelectWalletActivityViewModel::class.java)


        setupRecycleView()
        registerListener()

        add_wallet_fab.setOnClickListener {
            val addWalletActivity = Intent(this@SelectWalletActivity, WalletDetailActivity::class.java)
            startActivity(addWalletActivity)
        }

    }

    private fun registerListener()
    {
        viewModel.walletList.observe(this)
        {
            if(it!=null)
            {
                walletAdapter.walletList = it
            }
        }
    }

    private fun setupRecycleView()
    {
        walletAdapter.itemClickListener = {
            val result = Intent()
            result.putExtra(RESULT_WALLET_ID, it.id)
            setResult(RESULT_OK, result)
            finish()
        }

        wallet_list_rv.adapter = walletAdapter
        wallet_list_rv.layoutManager = LinearLayoutManager(this)
    }
}