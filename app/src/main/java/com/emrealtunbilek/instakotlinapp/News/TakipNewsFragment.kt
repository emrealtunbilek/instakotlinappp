package com.emrealtunbilek.instakotlinapp.News


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R


class TakipNewsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_takip_news, container, false)
    }

}
