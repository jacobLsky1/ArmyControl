package com.jacoblip.andriod.armycontrol

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.*
import com.jacoblip.andriod.armycontrol.utilities.AddingSoldierHelper
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.utilities.WifiReceiver
import com.jacoblip.andriod.armycontrol.views.*
import com.jacoblip.andriod.armycontrol.views.activities.AddOrEditNewActivityFragment
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment
import com.jacoblip.andriod.armycontrol.views.adapters.AddSoldierToDayAdapter
import com.jacoblip.andriod.armycontrol.views.soldiers.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity()
        , MainSoldiersFragment.ButtonCallbacks, MainSoldiersFragment.SoldierCallbacks,
         MainSoldiersFragment.SoldierSelectedFromRV, SoldierFragment.EditSoldierCallbacks,
        MainActivitiesFragment.OnActivityPressedCallBacks, MainFragment.AddActivityCallBacks,
        MainActivitiesFragment.OnActivitySelectedFromRVCallBacks,MainActivitiesFragment.OnDayLongPressCallBacks{

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    lateinit var soldiersViewModel:SoldiersViewModel
    lateinit var activitiesViewModel:ActivitiesViewModel
    lateinit var wifiReceiver:WifiReceiver
    lateinit var fragment: Fragment
    lateinit var removeButton: Button
    lateinit var preferences : SharedPreferences
    var listOfSoldiersSelected = mutableListOf<Soldier>()
    var listOfActivitysSelected = mutableListOf<ArmyActivity>()
    var commandPath = "1"
    var armyControlGroup = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_activity_main)
        removeButton = findViewById(R.id.deleteSoldierButton)
        navView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawerLayout)
        preferences = getSharedPreferences("armyControl", Context.MODE_PRIVATE)
        var intent = intent
        commandPath = intent.getStringExtra("commandPath").toString()
        armyControlGroup = preferences.getString("ArmyControlGroup","").toString()
        Util.groupRef = FirebaseDatabase.getInstance().getReference(armyControlGroup)
        Util.userCommandPath = commandPath

        val toolbar: Toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        setUpActionDrawer()

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
            if(listOfSoldiersSelected.isNotEmpty()) {
                activitiesViewModel.removeSoldiersFromDays(listOfSoldiersSelected.toList())
                soldiersViewModel.removeSoldiers(listOfSoldiersSelected.toList())
                soldiersViewModel.soldiersDeleted.postValue(true)
                Util.inSelectionMode.postValue(false)
                listOfSoldiersSelected = mutableListOf()
            }
            if(listOfActivitysSelected.isNotEmpty()){
                soldiersViewModel.removeActivitiesFromSoldiers(listOfActivitysSelected.toList())
                activitiesViewModel.removeActivities(listOfActivitysSelected.toList(),Util.currentDate.value!!)
                activitiesViewModel.activitiesDeleted.postValue(true)
                Util.inSelectionMode.postValue(false)
                listOfActivitysSelected = mutableListOf()
            }
        }
        fragment = commandPath?.let { MainFragment.newInstance(it) }!!
        setFragement(fragment)
    }

    fun setUpObservers(){
        Util.inSelectionMode.observe(this, Observer {
            if(it){
                removeButton.visibility = View.VISIBLE
                if(Util.inActivitiesFragment){
                    removeButton.text = "הסר פעילויות"
                }else{
                    removeButton.text = "הסר חיילים"
                }
            }else{
                removeButton.visibility = View.GONE
            }
        })

        Util.currentDate.observe(this, Observer {
                if(listOfActivitysSelected.isNotEmpty()){
                    listOfActivitysSelected = mutableListOf()
                    Util.inSelectionMode.postValue(false)
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
            listOfActivitysSelected = mutableListOf()
            listOfSoldiersSelected = mutableListOf()

            if(fragment is RVSoldiersFragment) {
                fragment.onBackPressed()
                super.onBackPressed()
                return
            }

        }else{
            if(fragment is SoldierFragment){
                fragment.onBackPressed()
                super.onBackPressed()
                return
            }
            if(supportFragmentManager.backStackEntryCount==0) {
              exitDialog()
            }else {
                super.onBackPressed()
            }
        }
    }

    private fun exitDialog(){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.a_alert_for_deleting_activitys, null)
        var textTV = dialogView.findViewById(R.id.alertTextTV) as TextView
        val yesButton = dialogView.findViewById(R.id.yesButton) as Button
        val noButton = dialogView.findViewById(R.id.noButton) as Button
        textTV.text = "האם תרצה לצאת מהאפליקציה?"

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setView(dialogView).setCancelable(false)

        val dialog = alertDialog.create()
        dialog.show()

        noButton.setOnClickListener {
            dialog.dismiss()
        }
        yesButton.setOnClickListener {
            dialog.dismiss()
            this.finish()
        }
    }

    override fun onActivityPressed(activity:ArmyActivity?) {
        fragment = AddOrEditNewActivityFragment.newInstance(activity,commandPath)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun addActivity() {
        onActivityPressed(null)
    }

    fun setUpActionDrawer(){
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.switchUserButton -> {
                        preferences.edit().putString("ArmyControlLoggedIn", "").apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.exitAppButton -> {
                        exitDialog()
                        true
                    }
                    else -> {true}
                }
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onActivitySelectedFromRV(activity: ArmyActivity,addActivity:Boolean) {
        if (addActivity) {
            listOfActivitysSelected.add(activity)
        } else {
            listOfActivitysSelected.remove(activity)
        }
        if(listOfActivitysSelected.size==0)
            Util.inSelectionMode.postValue(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDayLongPress(it: ArmyDay?) {
        val armyDay = it!!
        var allSoldiers = soldiersViewModel.listOfPersonalSoldiers.value
        val localDate = LocalDate.parse(armyDay!!.date)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.a_add_soldiers_for_day, null)
        var dateTV = dialogView.findViewById(R.id.dayTV) as TextView
        val continueButton = dialogView.findViewById(R.id.commitButton) as Button
        val deleteDayButton = dialogView.findViewById(R.id.deleteDayButton) as Button
        var soldiersRV = dialogView.findViewById(R.id.soldiersToAddRV) as ListView
        var signAllCB = dialogView.findViewById(R.id.signEveryOneCB) as CheckBox
        var signNoOneCB =     dialogView.findViewById(R.id.signNoOneCB) as CheckBox
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        dateTV.text = "${Util.getDayOfWeek(localDate.dayOfWeek.toString())} - ${armyDay.date}"
        deleteDayButton.isVisible = true

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setView(dialogView).setCancelable(false)

        val dialog = alertDialog.create()
        dialog.show()
        AddingSoldierHelper.soldiersToAdd = mutableListOf()

        signNoOneCB.isChecked = true

        if(armyDay.listOfSoldiers.isNotEmpty()) {
            AddingSoldierHelper.soldiersToAdd = armyDay.listOfSoldiers.toMutableList()
        }
        soldiersRV.adapter  = AddSoldierToDayAdapter(
            applicationContext,
            allSoldiers as List<Soldier>,
            false,
            false,
            armyDay.listOfSoldiers.toMutableList()
        )

        signAllCB.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked) {
                signNoOneCB.isChecked = false
                AddingSoldierHelper.soldiersToAdd = mutableListOf()
                AddingSoldierHelper.soldiersToAdd.addAll(getIDS(allSoldiers))
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    applicationContext,
                    allSoldiers as List<Soldier>,
                    true,
                    false
                    ,null
                )
            }
        }
        signNoOneCB.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked) {
                signAllCB.isChecked = false
                AddingSoldierHelper.soldiersToAdd = mutableListOf()
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    applicationContext,
                    allSoldiers as List<Soldier>,
                    false,
                    false,
                    null
                )
            }
        }

        continueButton.setOnClickListener {
            var listOfSoldiersToAdd = AddingSoldierHelper.soldiersToAdd
            val editedArmyDay = ArmyDay(armyDay.date, listOf(), listOfSoldiersToAdd.toList())
            AddingSoldierHelper.soldiersToAdd = mutableListOf()
                activitiesViewModel.editArmyDay(editedArmyDay)
                dialog.dismiss()
        }

       deleteDayButton.setOnClickListener {
            AddingSoldierHelper.soldiersToAdd = mutableListOf()
            activitiesViewModel.deleteArmyDay(armyDay)
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            AddingSoldierHelper.soldiersToAdd = mutableListOf()
            dialog.dismiss()
        }
    }

    fun getIDS(list:List<Soldier>):List<String>{
        var ids = mutableListOf<String>()
        for(soldier in list){
            ids.add(soldier.idNumber)
        }
        return ids
    }



}