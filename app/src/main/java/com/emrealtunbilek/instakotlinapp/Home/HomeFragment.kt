package com.emrealtunbilek.instakotlinapp.Home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * Created by Emre on 28.02.2018.
 */
class HomeFragment : Fragment() {

    lateinit var fragmentView:View
    private val ACTIVITY_NO=0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentView=inflater?.inflate(R.layout.fragment_home, container, false)
        return fragmentView
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
    }

    fun setupNavigationView(){

        var fragmentBottomNavView=fragmentView.bottomNavigationView

        BottomnavigationViewHelper.setupBottomNavigationView(fragmentBottomNavView)
        BottomnavigationViewHelper.setupNavigation(activity!!, fragmentBottomNavView)
        var menu=fragmentBottomNavView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}