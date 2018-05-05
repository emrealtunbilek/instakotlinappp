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
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.android.synthetic.main.activity_register.*


class TelefonKoduGirFragment : Fragment() {

    var gelenTelNo = ""
    lateinit var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID = ""
    var gelenKod=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater!!.inflate(R.layout.fragment_telefon_kodu_gir, container, false)

        view.tvKullaniciTelNo.setText(gelenTelNo)

        setupCallback()

        view.btnTelKodIleri.setOnClickListener {

            if(gelenKod.equals(view.etOnayKodu.text.toString())){
                EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(gelenTelNo,null, verificationID,gelenKod,false))
                var transaction=activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginContainer,KayitFragment())
                transaction.addToBackStack("kayitFragmentEklendi")
                transaction.commit()
            }else {
                Toast.makeText(activity,"Kod Hatalı",Toast.LENGTH_SHORT).show()
            }

        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                gelenTelNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this!!.activity!!,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


        return view
    }

    private fun setupCallback() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
              gelenKod = credential.smsCode!!
            }

            override fun onVerificationFailed(e: FirebaseException) {
                 Log.e("HATA","Hata çıktı: "+e.message)
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                verificationID =verificationId!!
            }
        }
    }

    @Subscribe (sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri : EventbusDataEvents.KayitBilgileriniGonder){

        gelenTelNo= kayitBilgileri.telNo!!
        Log.e("emre","Gelen tel no : "+gelenTelNo)

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
