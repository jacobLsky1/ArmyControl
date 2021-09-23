package com.jacoblip.andriod.armycontrol

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.jacoblip.andriod.armycontrol.data.sevices.*
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.utilities.WifiReceiver
import com.jacoblip.andriod.armycontrol.views.greeting.GreetingsFragment
import com.jacoblip.andriod.armycontrol.views.greeting.SignUpFragment

class LoginActivity:AppCompatActivity(), GreetingsFragment.Callbacks {

    lateinit var wifiReceiver: WifiReceiver
    lateinit var fragment: Fragment
    lateinit var prefs :SharedPreferences
    lateinit var repository: LogInRepository
    lateinit var viewModel: LogInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_activity_login)
        wifiReceiver = WifiReceiver()
        repository = LogInRepository()
        val viewModelProviderFactory = LogInViewModelProviderFactory(repository,applicationContext)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(LogInViewModel::class.java)
        prefs = getSharedPreferences("armyControl", Context.MODE_PRIVATE)
        //
        // prefs.edit().putString("ArmyControlVerified", "").apply()
        //

        setUpInternetObserver()
        seeIfLoggedIn()
        fragment = GreetingsFragment.newInstance(applicationContext,prefs)
        setFragement(fragment)
    }

    fun seeIfLoggedIn(){
        val commandPath = prefs.getString("ArmyControlLoggedIn","")
        if(commandPath!=""){
            var intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("commandPath",commandPath)
            startActivity(intent)
            finish()
        }

    }
    fun setUpInternetObserver(){
        Util.hasInternet.observe(this, Observer { hasInternet ->
            if (hasInternet){
            }
        })
    }

    fun setFragement(fragment: Fragment){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.login_fragment_container, fragment)
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

    override fun onButtonSelected() {
        val isVerified = prefs.getString("ArmyControlVerified","")
            setFragement(SignUpFragment.newInstance(applicationContext,prefs,isVerified!="verified"))

    }

}