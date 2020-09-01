package com.android.walletforest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.model.Repository

class RepoViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val constructor = modelClass.getDeclaredConstructor(Repository::class.java)
        return constructor.newInstance(repository)

    }
}