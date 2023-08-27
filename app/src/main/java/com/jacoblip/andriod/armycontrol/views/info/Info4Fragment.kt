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
import com.jacoblip.andriod.armycontrol.databinding.IFragmentInfo4Binding

class Info4Fragment(
):Fragment() {

    lateinit var viewModel: InfoViewModel
    lateinit var binding: IFragmentInfo4Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IFragmentInfo4Binding.inflate(layoutInflater)
        val view = binding.root
        view.apply {
            binding.imageView4.setImageResource(R.drawable.armyedit4)
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
        fun newInstance(): Info4Fragment {
            val args = Bundle()
            val fragment = Info4Fragment()
            fragment.arguments = args
            return fragment
        }

    }
}