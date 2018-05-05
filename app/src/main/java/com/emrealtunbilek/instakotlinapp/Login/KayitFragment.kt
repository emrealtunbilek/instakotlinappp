package com.emrealtunbilek.instakotlinapp.Login


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KayitFragment : Fragment() {

    var telNo = ""
    var verificationID = ""
    var gelenKod = ""
    var gelenEmail = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_kayit, container, false)
    }

    @Subscribe(sticky = true)
    internal fun onKayitEvent(kayitbilgileri: EventbusDataEvents.KayitBilgileriniGonder) {

        if (kayitbilgileri.emailkayit == true) {
            gelenEmail = kayitbilgileri.email!!

            Toast.makeText(activity,"Gelen email : "+gelenEmail,Toast.LENGTH_SHORT).show()
            Log.e("emre", "Gelen tel no : " + gelenEmail)
        } else {
            telNo = kayitbilgileri.telNo!!
            verificationID = kayitbilgileri.verificationID!!
            gelenKod=kayitbilgileri.code!!

            Toast.makeText(activity,"Gelen kod : "+gelenKod+" VerificationID:"+verificationID,Toast.LENGTH_SHORT).show()

        }


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
