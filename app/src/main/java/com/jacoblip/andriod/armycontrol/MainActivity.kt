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
import com.jacoblip.andriod.armycontrol.data.sevices.MainViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.Repository
import com.jacoblip.andriod.armycontrol.data.sevices.ViewModelProviderFactory
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.utilities.WifiReceiver
import com.jacoblip.andriod.armycontrol.views.*
import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity()
        ,MainSoldiersFragment.ButtonCallbacks,MainSoldiersFragment.SoldierCallbacks,MainFragment.AddSoldierCallBacks,RVSoldiersFragment.SoldierSelectedFromRV {

    lateinit var viewModel:MainViewModel
    lateinit var wifiReceiver:WifiReceiver
    lateinit var fragment: Fragment
    lateinit var removeButton: Button
    var listOfSoldiersSelected = mutableListOf<Soldier>()
    var commandPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        removeButton = findViewById(R.id.deleteSoldierButton)
        var intent = intent
        commandPath = intent.getStringExtra("commandPath").toString()
        Realm.init(this)
        var realmConfiguration = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfiguration)
        val repository = Repository()
        val viewModelProviderFactory = ViewModelProviderFactory(repository,applicationContext)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
        wifiReceiver = WifiReceiver()
        setUpObservers()
        removeButton.setOnClickListener {
            viewModel.deleteSoldiers(listOfSoldiersSelected.toList())
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
        viewModel.listOfAllSoldiers.observe(this, Observer {
            if(it!=null){
                viewModel.updateLists(it.soldiers,commandPath)
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

    override fun onButtonSelectedSelected(numberOfFragment: Int,soldierCallbacks: MainSoldiersFragment.SoldierCallbacks) {

        fragment = RVSoldiersFragment.newInstance(soldierCallbacks,numberOfFragment)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onSoldierSelectedSelected(soldier: Soldier, callBacks: MainSoldiersFragment.SoldierCallbacks,callBacks2:RVSoldiersFragment.SoldierSelectedFromRV?) {
        fragment = SoldierFragment.newInstance(soldier, callBacks,callBacks2)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun addSoldier() {
        fragment = AddSoldierFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onSoldierSelected(soldier: Soldier,addSoldier:Boolean) {
            if (addSoldier) {
                listOfSoldiersSelected.add(soldier)
            } else {
                listOfSoldiersSelected.remove(soldier)
            }
        if(listOfSoldiersSelected.size==0)
            Util.inSelectionMode.postValue(false)
    }

    override fun onBackPressed() {
        if(Util.inSelectionMode.value!!){
            Util.inSelectionMode.postValue(false)
            var fragment = viewModel.currentFragment
            if(fragment is RVSoldiersFragment) {
                fragment.onBackPressed()
            }
            if(fragment is SoldierFragment){
                fragment.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }
    }


}