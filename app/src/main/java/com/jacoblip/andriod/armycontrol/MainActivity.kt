package com.jacoblip.andriod.armycontrol

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.*
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.utilities.WifiReceiver
import com.jacoblip.andriod.armycontrol.views.*
import com.jacoblip.andriod.armycontrol.views.soldiers.*

class MainActivity : AppCompatActivity()
        , MainSoldiersFragment.ButtonCallbacks, MainSoldiersFragment.SoldierCallbacks,
        MainFragment.AddSoldierCallBacks, MainSoldiersFragment.SoldierSelectedFromRV,
        SoldierFragment.EditSoldierCallbacks{


    lateinit var soldiersViewModel:SoldiersViewModel
    lateinit var activitiesViewModel:ActivitiesViewModel
    lateinit var wifiReceiver:WifiReceiver
    lateinit var fragment: Fragment
    lateinit var removeButton: Button
    var listOfSoldiersSelected = mutableListOf<Soldier>()
    var commandPath = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        removeButton = findViewById(R.id.deleteSoldierButton)
        var intent = intent
        commandPath = intent.getStringExtra("commandPath").toString()
        Util.userCommandPath = commandPath

        //Realm.init(this)
        //var realmConfiguration = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        //Realm.setDefaultConfiguration(realmConfiguration)

        val repository = Repository()

        val viewModelProviderFactory1 = SoldiersViewModelProviderFactory(repository,applicationContext)
        val viewModelProviderFactory2 = ActivitiesViewModelProviderFactory(repository,applicationContext)

        soldiersViewModel = ViewModelProvider(this, viewModelProviderFactory1).get(SoldiersViewModel::class.java)
        activitiesViewModel = ViewModelProvider(this, viewModelProviderFactory2).get(ActivitiesViewModel::class.java)

        wifiReceiver = WifiReceiver()
        setUpObservers()
        removeButton.setOnClickListener {
            soldiersViewModel.removeSoldiers(listOfSoldiersSelected.toList())
            Util.inSelectionMode.postValue(false)
            listOfSoldiersSelected = mutableListOf()
        }
        fragment = commandPath?.let { MainFragment.newInstance(it) }!!
        setFragement(fragment)
    }

    fun setUpObservers(){
        Util.inSelectionMode.observe(this, Observer {
            if(it){
                removeButton.visibility = View.VISIBLE
            }else{
                removeButton.visibility = View.GONE
            }
        })
    }


    fun setFragement(fragment:Fragment){

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()

    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(wifiReceiver, filter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(wifiReceiver)
    }

    override fun onButtonSelectedSelected(numberOfFragment: Int, soldierCallbacks: MainSoldiersFragment.SoldierCallbacks, soldierRVCallbacks: MainSoldiersFragment.SoldierSelectedFromRV) {

        fragment = RVSoldiersFragment.newInstance(soldierCallbacks,numberOfFragment,soldierRVCallbacks)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onSoldierSelectedSelected(soldier: Soldier, callbacks1: MainSoldiersFragment.SoldierCallbacks, soldierSelectedCallbacks: MainSoldiersFragment.SoldierSelectedFromRV?) {
        fragment = SoldierFragment.newInstance(soldier,callbacks1,soldierSelectedCallbacks)
        soldiersViewModel.setSoldier(soldier)
        soldiersViewModel.soldierStack.push(soldier)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun addSoldier() {
        fragment = AddSoldierFragment.newInstance(commandPath)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onSoldierSelectedFromRV(soldier: Soldier,addSoldier:Boolean) {
            if (addSoldier) {
                listOfSoldiersSelected.add(soldier)
            } else {
                listOfSoldiersSelected.remove(soldier)
            }
        if(listOfSoldiersSelected.size==0)
            Util.inSelectionMode.postValue(false)
    }

    override fun onEditSoldierSelected(soldier: Soldier) {
        fragment = EditSoldierFragment.newInstance(soldier)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        var fragment = soldiersViewModel.currentFragment
        if(Util.inSelectionMode.value!!){
            Util.inSelectionMode.postValue(false)

            if(fragment is RVSoldiersFragment) {
                fragment.onBackPressed()
            }

        }else{
            if(fragment is SoldierFragment){
                fragment.onBackPressed()
            }
            super.onBackPressed()
        }
    }


}