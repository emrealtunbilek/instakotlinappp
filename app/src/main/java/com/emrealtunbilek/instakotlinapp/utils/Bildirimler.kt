package com.emrealtunbilek.instakotlinapp.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Created by Emre on 8.07.2018.
 */
object Bildirimler {

    private var mRef=FirebaseDatabase.getInstance().reference
    private var mAuth=FirebaseAuth.getInstance()
    private var mUserID=mAuth.currentUser!!.uid

     val YENI_TAKIP_ISTEGI=1
     val TAKIP_ISTEGINI_SIL=2

    fun bildirimKaydet(bildirimYapanUserID:String, bildirimTuru:Int){

        when(bildirimTuru){

            YENI_TAKIP_ISTEGI->{
                var yeniBildirimID=mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur","1")
                yeniBildirim.put("user_id", mUserID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(yeniBildirimID).setValue(yeniBildirim)
            }

            TAKIP_ISTEGINI_SIL->{

                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue()!!.equals("1") && bildirim.child("user_id").getValue()!!.equals(mUserID)){
                                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(okunanBildirimKey).removeValue()
                                break
                            }

                        }

                    }


                })

            }




        }




    }


}