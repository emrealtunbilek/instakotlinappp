package com.emrealtunbilek.instakotlinapp.Home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import com.emrealtunbilek.instakotlinapp.utils.HomePagerAdapter
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val ACTIVITY_NO=0
    private val TAG="HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initImageLoader()
        setupNavigationView()
        setupHomeViewPager()
    }

    fun setupNavigationView(){
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun setupHomeViewPager() {
        var homePagerAdapter=HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment()) //id = 0
        homePagerAdapter.addFragment(HomeFragment()) //id =1
        homePagerAdapter.addFragment(MessagesFragment())//id =2

        //activity mainde bulunan viewpagera olusturudugumuz adaptörü atadık
        homeViewPager.adapter=homePagerAdapter

        //viewpagerın homefragment ile baslamasını sagladık
        homeViewPager.setCurrentItem(1)

    }

    private fun initImageLoader(){

        var universalImageLoader= UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)

    }

}
