package com.jacoblip.andriod.armycontrol.views.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.sevices.InfoViewModel
import com.jacoblip.andriod.armycontrol.databinding.IFragmentInfo1Binding
import com.jacoblip.andriod.armycontrol.databinding.IFragmentInfo5Binding
import com.jacoblip.andriod.armycontrol.databinding.IFragmentInfo6Binding

class Info6Fragment(
):Fragment() {

    lateinit var viewModel: InfoViewModel
    lateinit var binding: IFragmentInfo6Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IFragmentInfo6Binding.inflate(layoutInflater)
        val view = binding.root
        view.apply {
            binding.imageView6.setImageResource(R.drawable.armyedit6)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(InfoViewModel::class.java)

        setUpFragment()
        setUpObservers()
    }

    private fun setUpFragment(){

    }

    private fun setUpObservers(){

    }



    companion object{
        fun newInstance(): Info6Fragment {
            val args = Bundle()
            val fragment = Info6Fragment()
            fragment.arguments = args
            return fragment
        }

    }
}