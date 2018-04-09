package com.emrealtunbilek.instakotlinapp.Profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view=inflater!!.inflate(R.layout.fragment_profile_edit, container, false)

        view.imgClose.setOnClickListener {

            activity.onBackPressed()

        }


        return view
    }

}
