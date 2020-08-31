package com.android.walletforest.model

import android.content.Context

class Repository private constructor(appContext: Context) {
    val appDatabase = AppDatabase.getInstance(appContext)




    companion object {
        var instance: Repository? = null

        fun getInstance(appContext: Context): Repository {
            synchronized(Repository::class.java)
            {
                return instance ?: Repository(appContext).also {
                    instance = it
                }
            }
        }
    }
}