package com.emrealtunbilek.instakotlinapp.utils

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

/**
 * Created by Emre on 25.02.2018.
 */
class BottomnavigationViewHelper {

    companion object {

        fun setupBottomNavigationView(bottomnavigationViewEx: BottomNavigationViewEx){

            bottomnavigationViewEx.enableAnimation(false)
            bottomnavigationViewEx.enableItemShiftingMode(false)
            bottomnavigationViewEx.enableShiftingMode(false)
            bottomnavigationViewEx.setTextVisibility(false)

        }


    }

}