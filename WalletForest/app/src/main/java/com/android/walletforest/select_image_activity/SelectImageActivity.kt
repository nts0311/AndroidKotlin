package com.android.walletforest.select_image_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.android.walletforest.R
import kotlinx.android.synthetic.main.activity_select_image.*

const val RESULT_ICON_ID = "result_icon_id"

class SelectImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)
        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        val iconAdapter = IconAdapter()

        iconAdapter.itemClickListener = {
            val result = Intent()
            result.putExtra(RESULT_ICON_ID, it)
            setResult(RESULT_OK, result)
            finish()
        }

        iconListRv.adapter = iconAdapter

        val layoutManager = GridLayoutManager(this,5,GridLayoutManager.VERTICAL,false)
        iconListRv.layoutManager = layoutManager
    }
}