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


        setupViewPager()
    }


    override fun onResume() {
        super.onResume()
        setupNavigationView()
    }
    private fun setupViewPager() {
        var tabAdlari = ArrayList<String>()
        tabAdlari.add("TAKIP")
        tabAdlari.add("SEN")

        var newsPagerAdapter = NewsPagerAdapter(supportFragmentManager, tabAdlari)
        newsPagerAdapter.addFragment(TakipNewsFragment())
        newsPagerAdapter.addFragment(SenNewsFragment())

        viewPagerNews.adapter=newsPagerAdapter

        if(intent.extras!=null){
            if(intent.extras.getString("bildirim").equals("yeni_takip_istegi")){
                viewPagerNews.setCurrentItem(1)
            }
        }


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


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }
    fun setupNavigationView(){

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView,ACTIVITY_NO)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
