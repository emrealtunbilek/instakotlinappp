package com.emrealtunbilek.instakotlinapp.Search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_algolia_search.*
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher



class AlgoliaSearchActivity : AppCompatActivity() {

    private val ACTIVITY_NO=1
    private val TAG="AlgoliaSearchActivity"
    private val ALGOLIA_APP_ID = "60M8DCAPLT"
    private val ALGOLIA_SEARCH_API_KEY = "3c832a8c9c865059604c76bc54f44644"
    private val ALGOLIA_INDEX_NAME = "KotlinInstagram"
    lateinit var searcher:Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algolia_search)

        setupNavigationView()
        setupAlgoliaSearch()

    }

    private fun setupAlgoliaSearch() {
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        val helper = InstantSearch(this, searcher)
        helper.search()

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun setupNavigationView(){

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }
}
