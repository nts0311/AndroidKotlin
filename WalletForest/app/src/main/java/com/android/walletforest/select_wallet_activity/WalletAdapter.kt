package com.android.walletforest.select_wallet_activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R
import com.android.walletforest.model.Entities.Wallet


class WalletAdapter : RecyclerView.Adapter<WalletViewHolder>() {

    var walletList: List<Wallet> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var itemClickListener : (Wallet) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder =
        WalletViewHolder.from(parent)

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(walletList[position], itemClickListener)
    }

    override fun getItemCount(): Int = walletList.size
}

class WalletViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    private val walletIcon = root.findViewById<ImageView>(R.id.wallet_icon_img)
    private val walletName = root.findViewById<TextView>(R.id.wallet_name_txt)
    private val walletBalance = root.findViewById<TextView>(R.id.wallet_balance_txt)

    fun bind(wallet: Wallet, itemClickListener : (Wallet) -> Unit) {
        root.setOnClickListener {
            itemClickListener.invoke(wallet)
        }

        walletIcon.setImageResource(wallet.imageId)
        walletName.text = wallet.name
        walletBalance.text = wallet.amount.toString()
    }

    companion object {
        fun from(parent: ViewGroup): WalletViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val root = inflater.inflate(R.layout.item_wallet, parent, false)
            return WalletViewHolder(root)
        }
    }
}