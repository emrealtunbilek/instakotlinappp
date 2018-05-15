package com.emrealtunbilek.instakotlinapp.Login


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.fragment_kayit.*
import kotlinx.android.synthetic.main.fragment_kayit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KayitFragment : Fragment() {

    var telNo = ""
    var verificationID = ""
    var gelenKod = ""
    var gelenEmail = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view= inflater!!.inflate(R.layout.fragment_kayit, container, false)

        view.etAdSoyad.addTextChangedListener(watcher)
        view.etKullaniciAdi.addTextChangedListener(watcher)
        view.etSifre.addTextChangedListener(watcher)


        return view
    }

    var watcher : TextWatcher = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
           if(s!!.length>5){

               if(etAdSoyad.text.toString().length>5 && etKullaniciAdi.text.toString().length>5 && etSifre.text.toString().length>5){

                   btnGiris.isEnabled=true
                   btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.beyaz))
                   btnGiris.setBackgroundResource(R.drawable.register_button_aktif)

               }else {
                   btnGiris.isEnabled=false
                   btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.sonukmavi))
                   btnGiris.setBackgroundResource(R.drawable.register_button)
               }


           }else {
               btnGiris.isEnabled=false
               btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.sonukmavi))
               btnGiris.setBackgroundResource(R.drawable.register_button)
           }
        }

    }

    @Subscribe(sticky = true)
    internal fun onKayitEvent(kayitbilgileri: EventbusDataEvents.KayitBilgileriniGonder) {

        if (kayitbilgileri.emailkayit == true) {
            gelenEmail = kayitbilgileri.email!!

            Toast.makeText(activity,"Gelen email : "+gelenEmail,Toast.LENGTH_SHORT).show()
            Log.e("emre", "Gelen email : " + gelenEmail)
        } else {
            telNo = kayitbilgileri.telNo!!
            verificationID = kayitbilgileri.verificationID!!
            gelenKod=kayitbilgileri.code!!

            Toast.makeText(activity,"Gelen kod : "+gelenKod+" VerificationID: "+verificationID,Toast.LENGTH_SHORT).show()

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
