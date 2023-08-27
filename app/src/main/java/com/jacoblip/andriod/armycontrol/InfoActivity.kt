package com.jacoblip.andriod.armycontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jacoblip.andriod.armycontrol.data.sevices.*
import com.jacoblip.andriod.armycontrol.databinding.ActivityInfoBinding
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.info.*

class InfoActivity : AppCompatActivity() {

    private lateinit var viewModel: InfoViewModel
    var frg1 = Info1Fragment.newInstance()
    var frg2 = Info2Fragment.newInstance()
    var frg3 = Info3Fragment.newInstance()
    var frg4 = Info4Fragment.newInstance()
    var frg5 = Info5Fragment.newInstance()
    var frg6 = Info6Fragment.newInstance()
    var frg7 = Info7Fragment.newInstance()

    private lateinit var binding: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val repository = Repository()
        val viewModelProviderFactory = InfoViewModelProviderFactory(repository,applicationContext)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(InfoViewModel::class.java)
        viewModel.initNum()
        setUpObservers()
        binding.infoBackButton.setOnClickListener {
            viewModel.decreaseNum()
        }
        binding.infoNextButton.setOnClickListener {
            viewModel.increaseNum()
        }
    }

    fun setUpObservers(){
        viewModel.infoPage.observe(this, Observer {
            when(it){
                0->{
                    setFragment(frg1)
                    binding.infoBackButton.isEnabled = false
                }
                1->{
                    setFragment(frg2)
                    binding.infoBackButton.isEnabled = true
                }
                2->{setFragment(frg3)}
                3->{setFragment(frg4)}
                4->{setFragment(frg5)}
                5->{
                    setFragment(frg6)
                    binding.infoNextButton.text = "הבא"
                }
                6->{
                    setFragment(frg7)
                    binding.infoNextButton.text = "סיום"
                }
                7->{ this.finish() }

            }
        })
    }

    private fun setFragment(fragment: Fragment){

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.info_fragment_container, fragment)
            .commit()


    }
}