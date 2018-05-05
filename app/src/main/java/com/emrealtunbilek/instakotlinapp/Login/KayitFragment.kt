package com.emrealtunbilek.instakotlinapp.Login


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KayitFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_kayit, container, false)
    }

    @Subscribe(sticky = true)
    internal fun onTelefonNoEvent(emailAdres : EventbusDataEvents.EmailGonder){

        var gelenEmail=emailAdres.email
        Log.e("emre","Gelen tel no : "+gelenEmail)

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
