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
     val TAKIP_ETMEYE_BASLADI=3
     val TAKIP_ETMEYI_BIRAKTI=4
     val GONDERI_BEGENILDI=5
     val GONDERI_BEGENISI_GERI_CEK=6
     val BANA_GELEN_TAKIP_ISTEGINI_SIL=7
     val BIRI_BENI_TAKIBE_BASLADI=8
     val TAKIP_ISTEGI_ONAYLANDI=9


    fun bildirimKaydet(bildirimYapanUserID:String, bildirimTuru:Int){

        when(bildirimTuru){

            YENI_TAKIP_ISTEGI->{
                var yeniBildirimID=mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", YENI_TAKIP_ISTEGI)
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
                            //Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == YENI_TAKIP_ISTEGI && bildirim.child("user_id").getValue()!!.equals(mUserID)){
                                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(okunanBildirimKey).removeValue()
                                break
                            }

                        }

                    }


                })

            }

            TAKIP_ETMEYE_BASLADI->{


                var yeniBildirimID=mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", TAKIP_ETMEYE_BASLADI)
                yeniBildirim.put("user_id", mUserID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(yeniBildirimID).setValue(yeniBildirim)

                var yeniBildirimTakipEttigim=HashMap<String,Any>()
                yeniBildirimTakipEttigim.put("bildirim_tur", TAKIP_ETMEYE_BASLADI)
                yeniBildirimTakipEttigim.put("user_id", bildirimYapanUserID)
                yeniBildirimTakipEttigim.put("time", ServerValue.TIMESTAMP)
                mRef.child("takip_ettiklerimin_bildirimleri").child(mUserID).child(yeniBildirimID).setValue(yeniBildirimTakipEttigim)


            }

            TAKIP_ETMEYI_BIRAKTI->{

                var silindiMi=false
                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            //Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == TAKIP_ETMEYE_BASLADI && bildirim.child("user_id").getValue()!!.equals(mUserID)){
                                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(okunanBildirimKey).removeValue()
                                silindiMi=true

                            }

                        }

                    }


                })

                mRef.child("benim_bildirimlerim").child(mUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            //Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == TAKIP_ISTEGI_ONAYLANDI && bildirim.child("user_id").getValue()!!.equals(bildirimYapanUserID)){
                                mRef.child("benim_bildirimlerim").child(mUserID).child(okunanBildirimKey).removeValue()
                                silindiMi=true


                            }

                        }

                    }


                })



                mRef.child("takip_ettiklerimin_bildirimleri").child(mUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            //Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == TAKIP_ETMEYE_BASLADI && bildirim.child("user_id").getValue()!!.equals(bildirimYapanUserID)){
                                mRef.child("takip_ettiklerimin_bildirimleri").child(mUserID).child(okunanBildirimKey).removeValue()
                                break
                            }

                        }

                    }


                })

            }

            BANA_GELEN_TAKIP_ISTEGINI_SIL->{
                mRef.child("benim_bildirimlerim").child(mAuth.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            //Log.e("VVV",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == YENI_TAKIP_ISTEGI && bildirim.child("user_id").getValue()!!.equals(bildirimYapanUserID)){
                                mRef.child("benim_bildirimlerim").child(mAuth.uid).child(okunanBildirimKey).removeValue()
                                break
                            }

                        }
                    }

                })
            }

            BIRI_BENI_TAKIBE_BASLADI->{

                var yeniBildirimID=mRef.child("benim_bildirimlerim").child(mUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", TAKIP_ETMEYE_BASLADI)
                yeniBildirim.put("user_id", bildirimYapanUserID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                mRef.child("benim_bildirimlerim").child(mUserID).child(yeniBildirimID).setValue(yeniBildirim)

                var yeniBildirimTakipEttigim=HashMap<String,Any>()
                yeniBildirimTakipEttigim.put("bildirim_tur", TAKIP_ETMEYE_BASLADI)
                yeniBildirimTakipEttigim.put("user_id", mUserID)
                yeniBildirimTakipEttigim.put("time", ServerValue.TIMESTAMP)
                mRef.child("takip_ettiklerimin_bildirimleri").child(bildirimYapanUserID).child(yeniBildirimID).setValue(yeniBildirimTakipEttigim)


            }

            TAKIP_ISTEGI_ONAYLANDI->{
                var yeniBildirimID=mRef.child("benim_bildirimlerim").child(mUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", TAKIP_ISTEGI_ONAYLANDI)
                yeniBildirim.put("user_id", bildirimYapanUserID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                //Log.e("FCM","BİLDİRİM KAYDEDILDI")
                mRef.child("benim_bildirimlerim").child(mUserID).child(yeniBildirimID).setValue(yeniBildirim)
            }





        }

    }

    fun bildirimKaydet(bildirimYapanUserID:String, bildirimTuru:Int, gonderiID:String){

        when(bildirimTuru){

            GONDERI_BEGENILDI->{

                var yeniBildirimID=mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", GONDERI_BEGENILDI)
                yeniBildirim.put("user_id", mUserID)
                yeniBildirim.put("gonderi_id",gonderiID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(yeniBildirimID).setValue(yeniBildirim)

                var yeniBildirimTakipEttigim=HashMap<String,Any>()
                yeniBildirimTakipEttigim.put("bildirim_tur", GONDERI_BEGENILDI)
                yeniBildirimTakipEttigim.put("user_id", bildirimYapanUserID)
                yeniBildirimTakipEttigim.put("gonderi_id",gonderiID)
                yeniBildirimTakipEttigim.put("time", ServerValue.TIMESTAMP)
                mRef.child("takip_ettiklerimin_bildirimleri").child(mUserID).child(yeniBildirimID).setValue(yeniBildirimTakipEttigim)
            }

            GONDERI_BEGENISI_GERI_CEK->{

                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).orderByChild("gonderi_id").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            //Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == GONDERI_BEGENILDI && bildirim.child("gonderi_id").getValue()!!.equals(gonderiID)){
                                mRef.child("benim_bildirimlerim").child(bildirimYapanUserID).child(okunanBildirimKey).removeValue()
                                break
                            }

                        }

                    }


                })

                mRef.child("takip_ettiklerimin_bildirimleri").child(mUserID).orderByChild("gonderi_id").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (bildirim in p0!!.children){

                            var okunanBildirimKey=bildirim!!.key
                            //Log.e("KONTROL",bildirim.toString())
                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == GONDERI_BEGENILDI && bildirim.child("gonderi_id").getValue()!!.equals(gonderiID)){
                                mRef.child("takip_ettiklerimin_bildirimleri").child(mUserID).child(okunanBildirimKey).removeValue()
                                break
                            }

                        }

                    }


                })

            }


        }


    }


}