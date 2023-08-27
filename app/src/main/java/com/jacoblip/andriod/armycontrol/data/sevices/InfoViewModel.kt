package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay


class InfoViewModel(repository: Repository, context: Context):ViewModel() {

    private var _infoPage = MutableLiveData(0 )
    var infoPage: LiveData<Int> = _infoPage

    fun initNum(){
        _infoPage.postValue(0)
    }

    fun increaseNum(){
        var num = infoPage.value
        _infoPage.postValue(num!!+1)
    }

    fun decreaseNum(){
        var num = infoPage.value
        _infoPage.postValue(num!!-1)
    }
}