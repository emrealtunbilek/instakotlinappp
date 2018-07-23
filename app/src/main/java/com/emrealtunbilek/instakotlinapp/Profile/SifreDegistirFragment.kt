package com.emrealtunbilek.instakotlinapp.Profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.emrealtunbilek.instakotlinapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
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


            if(!mevcutSifre.isNullOrEmpty() && mevcutSifre.length>=6){

                var myUser=FirebaseAuth.getInstance().currentUser
                if(myUser != null){
                    var credential=EmailAuthProvider.getCredential(myUser!!.email.toString(),mevcutSifre)
                    myUser.reauthenticate(credential).addOnCompleteListener(object : OnCompleteListener<Void>{
                        override fun onComplete(p0: Task<Void>) {
                            if(p0!!.isSuccessful){

                                if(yeniSifre.equals(yeniSifreTekrar)){

                                    if(!yeniSifre.isNullOrEmpty() && yeniSifre.length>=6){

                                        var myUser=FirebaseAuth.getInstance().currentUser
                                        myUser!!.updatePassword(yeniSifre).addOnCompleteListener(object : OnCompleteListener<Void>{
                                            override fun onComplete(p0: Task<Void>) {
                                                if(p0!!.isSuccessful){
                                                    Toast.makeText(activity,"Şifreniz güncellendi",Toast.LENGTH_SHORT).show()
                                                }else {
                                                    Toast.makeText(activity,"Şifre güncellenemedi",Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                        })


                                    }else {
                                        Toast.makeText(activity,"Yeni şifre en az 6 karakter olmalıdır",Toast.LENGTH_SHORT).show()
                                    }

                                }else {
                                    Toast.makeText(activity,"Şifreler eşleşmiyor",Toast.LENGTH_SHORT).show()
                                }



                            }else {
                                Toast.makeText(activity,"Mevcut şifreniz yanlış",Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }


            }else {
                Toast.makeText(activity,"Mevcut şifre en az 6 karakter olmalıdır",Toast.LENGTH_SHORT).show()
            }




        }

        return myView
    }

}
