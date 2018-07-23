package com.emrealtunbilek.instakotlinapp.Profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.fragment_sifre_degistir.view.*


class SifreDegistirFragment : Fragment() {

    var myView:View?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        myView= inflater.inflate(R.layout.fragment_sifre_degistir, container, false)

        myView!!.imgKapat.setOnClickListener {
            activity!!.onBackPressed()
        }

        myView!!.imgSifreDegistir.setOnClickListener {


            var mevcutSifre=myView!!.etMevcutSifre!!.text!!.toString()
            var yeniSifre=myView!!.etYeniSifre!!.text!!.toString()
            var yeniSifreTekrar=myView!!.eetYeniSifreTekrar!!.text!!.toString()





        }

        return myView
    }

}
