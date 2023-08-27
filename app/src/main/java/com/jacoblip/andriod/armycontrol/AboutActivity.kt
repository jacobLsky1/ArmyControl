package com.jacoblip.andriod.armycontrol

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jacoblip.andriod.armycontrol.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.companyName.text = getString(R.string.sample_company_name) + " v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
    }
}