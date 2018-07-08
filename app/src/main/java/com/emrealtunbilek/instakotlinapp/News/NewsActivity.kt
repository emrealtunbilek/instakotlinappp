package com.emrealtunbilek.instakotlinapp.News

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    private val ACTIVITY_NO=3
    private val TAG="NewsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        setupNavigationView()
        setupViewPager()
    }

    private fun setupViewPager() {
        var tabAdlari = ArrayList<String>()
        tabAdlari.add("TAKIP")
        tabAdlari.add("SEN")

        var newsPagerAdapter = NewsPagerAdapter(supportFragmentManager, tabAdlari)
        newsPagerAdapter.addFragment(TakipNewsFragment())
        newsPagerAdapter.addFragment(SenNewsFragment())

        viewPagerNews.adapter=newsPagerAdapter

        viewPagerNews.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

        })

        tabNews.setupWithViewPager(viewPagerNews)


    }

    fun setupNavigationView(){

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
