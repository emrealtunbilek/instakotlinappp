package com.emrealtunbilek.instakotlinapp.Generic


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.UserPosts

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TekGonderiFragment : Fragment() {

    var myView:View?=null
    var secilenGonderi:UserPosts?=null
    var videoMu:Boolean?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        myView=inflater.inflate(R.layout.fragment_tek_gonderi, container, false)

        Log.e("CCC","SECİLEN GONDERİ:"+secilenGonderi.toString())
        Log.e("CCC","SECİLEN GONDERİ videomu :"+videoMu)


        return myView
    }


    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onSecilenDosyaEvent(secilenGonderiNesnesi: EventbusDataEvents.SecilenGonderiyiGonder) {
        secilenGonderi = secilenGonderiNesnesi!!.secilenGonderi
        videoMu = secilenGonderiNesnesi!!.videoMu
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }


}
