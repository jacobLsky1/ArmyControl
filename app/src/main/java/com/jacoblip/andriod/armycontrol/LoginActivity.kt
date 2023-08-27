package com.jacoblip.andriod.armycontrol

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.jacoblip.andriod.armycontrol.data.sevices.*
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.utilities.WifiReceiver
import com.jacoblip.andriod.armycontrol.views.greeting.AuthFragment
import com.jacoblip.andriod.armycontrol.views.greeting.GreetingsFragment
import com.jacoblip.andriod.armycontrol.views.greeting.NewGroupFragment
import com.jacoblip.andriod.armycontrol.views.greeting.SignUpFragment

class LoginActivity:AppCompatActivity(), GreetingsFragment.Callbacks,
    GreetingsFragment.NewGroupCallBacks,NewGroupFragment.Callbacks,
    NewGroupFragment.CodeCallback,AuthFragment.ContinueCallbacks,
    NewGroupFragment.SoldierCodeCallback{

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



        //reset()



        setUpInternetObserver()
        seeIfLoggedIn()

        var isVerified = prefs.getString("ArmyControlVerified","")
        if(isVerified!=""){
            fragment = GreetingsFragment.newInstance(applicationContext,prefs)
            setFragement(fragment)
        }else{
            fragment = AuthFragment.newInstance(applicationContext,prefs)
            setFragement(fragment)
        }

    }

    private fun seeIfLoggedIn(){
        val commandPath = prefs.getString("ArmyControlLoggedIn","")
        if(commandPath!=""){
            var intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("commandPath",commandPath)
            startActivity(intent)
            finish()
        }

    }
    private fun setUpInternetObserver(){
        Util.hasInternet.observe(this, Observer { hasInternet ->
            if (hasInternet){
            }
        })
    }

    fun setFragement(fragment: Fragment){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.login_fragment_container, fragment)
                .addToBackStack(null)
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

    override fun onNewGroupButtonSelected() {
        setFragement(NewGroupFragment.newInstance(prefs))
    }

    override fun onDataReady() {
        val isVerified = prefs.getString("ArmyControlVerified","")
        setFragement(SignUpFragment.newInstance(applicationContext,prefs,isVerified!="verified"))
    }

    override fun onCodeReady(num: String) {
        groupCodeDialog(num)
    }

    private fun groupCodeDialog(num: String){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.m_alert_codes, null)
        var textTV = dialogView.findViewById(R.id.okSavedTextTV) as TextView
        val yesButton = dialogView.findViewById(R.id.okSavedButton) as Button
        textTV.text = " הקוד פלוגה שלך הוא :$num  שמור אותו הוא חשוב!"

        val alertDialog = AlertDialog.Builder(this@LoginActivity)
        alertDialog.setView(dialogView).setCancelable(false)

        val dialog = alertDialog.create()
        dialog.show()

        yesButton.text = "שמרתי ורשמתי"
        yesButton.setOnClickListener {
            prefs.edit().putString("groupNotInit","initiated").apply()
            prefs.edit().putString("valid",num).apply()
            dialog.dismiss()
        }
    }

    private fun soldierCodeDialog(num: String){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.m_alert_codes, null)
        var textTV = dialogView.findViewById(R.id.okSavedTextTV) as TextView
        val yesButton = dialogView.findViewById(R.id.okSavedButton) as Button
        textTV.text = " הקוד מפקד זמני שלך הוא :$num שמור אותו הוא חשוב!"

        val alertDialog = AlertDialog.Builder(this@LoginActivity)
        alertDialog.setView(dialogView).setCancelable(false)

        val dialog = alertDialog.create()
        dialog.show()

        yesButton.text = "שמרתי ורשמתי"
        yesButton.setOnClickListener {
            prefs.edit().putString("groupNotInit","initiated").apply()
            dialog.dismiss()
        }
    }

    override fun onContinue() {
        fragment = GreetingsFragment.newInstance(applicationContext,prefs)
        setFragement(fragment)
    }

    override fun onBackPressed() {
        if(fragment is AuthFragment || fragment is GreetingsFragment){
            this.finish()
        }
        if(fragment is SignUpFragment || fragment is NewGroupFragment) {
            super.onBackPressed()
        }
    }

    override fun onSoldierCodeReady(num: String) {
        soldierCodeDialog(num)
    }
    private fun reset(){
        prefs.edit().putString("ArmyControlLoggedIn", "").apply()
        prefs.edit().putString("ArmyControlVerified", "").apply()
        prefs.edit().putString("groupNotInit","").apply()
        prefs.edit().putString("firstTime","").apply()
    }

}