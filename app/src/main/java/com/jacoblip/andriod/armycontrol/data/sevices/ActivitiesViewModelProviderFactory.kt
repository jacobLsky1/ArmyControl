package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActivitiesViewModelProviderFactory(
    val repository: Repository,
    val context: Context
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ActivitiesViewModel(repository,context) as T
    }
}