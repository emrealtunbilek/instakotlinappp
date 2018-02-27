package com.emrealtunbilek.instakotlinapp.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Emre on 28.02.2018.
 */
class HomePagerAdapter(fm:FragmentManager): FragmentPagerAdapter(fm) {

    private var mFragmentList:ArrayList<Fragment> = ArrayList()


    //fragment pager adapterın yazmayı zorunlu kıldıgı fonksiyon
    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    //fragment pager adapterın yazmayı zorunlu kıldıgı fonksiyon
    override fun getCount(): Int {
        return mFragmentList.size
    }

    //kişisel fonksiyon
    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)
    }
}