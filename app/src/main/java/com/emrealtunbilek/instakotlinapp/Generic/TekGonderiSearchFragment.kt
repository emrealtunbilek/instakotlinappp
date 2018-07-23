package com.emrealtunbilek.instakotlinapp.Generic


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.Profile.ProfileActivity
import com.emrealtunbilek.instakotlinapp.Profile.ProfileSettingsActivity

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.Search.SearchActivity
import com.emrealtunbilek.instakotlinapp.utils.Bildirimler
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.emrealtunbilek.instakotlinapp.utils.TimeAgo
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_tek_gonderi_search.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class TekGonderiSearchFragment : Fragment() {

    var myView:View?=null
    var secilenGonderi: UserPosts?=null
    var videoMu:Boolean?=null
    val ACTIVITY_NO =4

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        myView=inflater.inflate(R.layout.fragment_tek_gonderi_search, container, false)

        Log.e("CCC","SECİLEN GONDERİ:"+secilenGonderi.toString())
        Log.e("CCC","SECİLEN GONDERİ videomu :"+videoMu)

        var tumLayout = myView as ConstraintLayout



        var olusturulanElemanVideoMu=false

        setData(tumLayout, secilenGonderi!!,videoMu!!)

        myView!!.imgClose.setOnClickListener{
            myView!!.videoView.stopPlayback()

            activity!!.onBackPressed()
        }





        return myView
    }



    fun setData(tumLayout:ConstraintLayout, oankiGonderi: UserPosts, videoMu: Boolean) {

        var profileImage = tumLayout.imgUserProfile
        var userNameTitle = tumLayout.tvKullaniciAdiBaslik
        var gonderi = tumLayout.imgPostResim
        var userNameveAciklama = tumLayout.tvKullaniciAdiveAciklama
        var gonderiKacZamanOnce = tumLayout.tvKacZamanOnce
        var yorumYap = tumLayout.imgYorum
        var gonderiBegen = tumLayout.imgBegen
        var mInstaLikeView = tumLayout.insta_like_view
        var begenmeSayisi = tumLayout.tvBegenmeSayisi
        var yorumlariGoster = tumLayout.tvYorumlariGoster
        var myVideo = tumLayout.videoView

        var gonderiTuru=tumLayout.textView4
        var myHomeActivity=activity


        if(videoMu){
            myVideo.visibility=View.VISIBLE
            gonderi.visibility=View.GONE
            gonderiTuru.setText("Keşfet")
            myVideo.setVideoPath(oankiGonderi!!.postURL)
            myVideo.start()
        }else {
            myVideo.visibility=View.GONE
            gonderi.visibility=View.VISIBLE
            gonderiTuru.setText("Keşfet")
            UniversalImageLoader.setImage(oankiGonderi.postURL!!, gonderi, null, "")
        }




        userNameTitle.setText(oankiGonderi.userName)

        userNameTitle.setOnClickListener {

            var tiklanilanUserID=oankiGonderi.userID

            if(!tiklanilanUserID!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                var intent= Intent(myHomeActivity,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("secilenUserID", oankiGonderi.userID)
                myHomeActivity!!.startActivity(intent)
            }else {

                var intent=Intent(myHomeActivity, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                myHomeActivity!!.startActivity(intent)
            }


        }


        userNameveAciklama.setText(oankiGonderi.userName.toString()+" "+oankiGonderi.postAciklama.toString())

        UniversalImageLoader.setImage(oankiGonderi.userPhotoURL!!, profileImage, null, "")
        gonderiKacZamanOnce.setText(TimeAgo.getTimeAgo(oankiGonderi.postYuklenmeTarih!!))

        begeniKontrol(gonderiBegen, begenmeSayisi, oankiGonderi)
        yorumlariGoruntule(yorumlariGoster, oankiGonderi)


        yorumYap.setOnClickListener {


            myVideo.stopPlayback()

            yorumlarFragmentiniBaslat(oankiGonderi)
        }

        yorumlariGoster.setOnClickListener {

            myVideo.stopPlayback()

            yorumlarFragmentiniBaslat(oankiGonderi)
        }

        gonderiBegen.setOnClickListener {

            var mRef = FirebaseDatabase.getInstance().reference
            var userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("likes").child(oankiGonderi.postID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.hasChild(userID)) {

                        mRef.child("likes").child(oankiGonderi.postID).child(userID).removeValue()
                        Bildirimler.bildirimKaydet(oankiGonderi.userID!!,Bildirimler.GONDERI_BEGENISI_GERI_CEK,oankiGonderi!!.postID!!)
                        Log.e("VVV","HA BEGENME gerı cekme BILDIRIM:"+oankiGonderi!!.postID)
                        gonderiBegen.setImageResource(R.drawable.ic_like)

                    } else {

                        mRef.child("likes").child(oankiGonderi.postID).child(userID).setValue(userID)


                        if(!oankiGonderi.userID!!.equals(userID))
                            Bildirimler.bildirimKaydet(oankiGonderi.userID!!,Bildirimler.GONDERI_BEGENILDI,oankiGonderi!!.postID!!)

                        Log.e("VVV","HA BEGENME BILDIRIM:"+oankiGonderi!!.postID)
                        gonderiBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                        mInstaLikeView.start()
                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.setText(""+p0!!.childrenCount!!.toString()+" beğenme")
                    }
                }


            })


        }

        var ilkTiklama: Long = 0
        var sonTiklama: Long = 0

        gonderi.setOnClickListener {

            ilkTiklama = sonTiklama
            sonTiklama = System.currentTimeMillis()

            if (sonTiklama - ilkTiklama < 300) {
                mInstaLikeView.start()

                FirebaseDatabase.getInstance().getReference().child("likes").child(oankiGonderi.postID)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(FirebaseAuth.getInstance().currentUser!!.uid)


                sonTiklama = 0
            }


        }

        myVideo.setOnClickListener {

            ilkTiklama = sonTiklama
            sonTiklama = System.currentTimeMillis()

            if (sonTiklama - ilkTiklama < 300) {
                mInstaLikeView.start()

                FirebaseDatabase.getInstance().getReference().child("likes").child(oankiGonderi.postID)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(FirebaseAuth.getInstance().currentUser!!.uid)


                sonTiklama = 0
            }


        }


    }

    fun begeniKontrol(gonderiBegen: ImageView, begenmeSayisi: TextView, oankiGonderi: UserPosts) {

        var mRef = FirebaseDatabase.getInstance().reference
        var userID = FirebaseAuth.getInstance().currentUser!!.uid
        mRef.child("likes").child(oankiGonderi.postID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                if(p0!!.getValue()!=null){
                    begenmeSayisi.visibility=View.VISIBLE
                    begenmeSayisi.setText(""+p0!!.childrenCount!!.toString()+" beğenme")
                }else {
                    begenmeSayisi.visibility=View.GONE
                }

                if (p0!!.hasChild(userID)) {
                    gonderiBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                } else {
                    gonderiBegen.setImageResource(R.drawable.ic_like)
                }
            }


        })


    }

    fun yorumlariGoruntule(yorumlariGoster:TextView, oankiGonderi: UserPosts){

        var mRef=FirebaseDatabase.getInstance().reference
        mRef.child("comments").child(oankiGonderi!!.postID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                var yorumSayisi=0

                for(ds in p0!!.children){
                    if(!ds!!.key.toString().equals(oankiGonderi!!.postID)){
                        yorumSayisi++
                    }
                }


                if(yorumSayisi >= 1){
                    yorumlariGoster.visibility=View.VISIBLE
                    yorumlariGoster.setText(yorumSayisi.toString()+" yorumun tümünü gör")
                }else {
                    yorumlariGoster.visibility=View.GONE
                }

            }


        })

    }

    fun yorumlarFragmentiniBaslat(oankiGonderi: UserPosts) {

//profile settings activity veya begendigimgonderiler için geçerli
        if(activity is ProfileSettingsActivity){
            (activity as ProfileSettingsActivity).profileSettingsRoot.visibility= View.GONE
            (activity as ProfileSettingsActivity).profileSettingsContainer.visibility=View.VISIBLE
            EventBus.getDefault().postSticky(EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder(oankiGonderi!!.postID))
            var transaction=(activity as ProfileSettingsActivity).supportFragmentManager.beginTransaction()
            transaction.hide( (activity as ProfileSettingsActivity).supportFragmentManager.findFragmentByTag("fra2"))
            transaction.add(R.id.profileSettingsContainer,CommentFragment())
            transaction.addToBackStack("CommentFragment")
            transaction.commit()
        }else  if(activity is SearchActivity){
            (activity as SearchActivity).tumLayout.visibility= View.GONE
            (activity as SearchActivity).frameLayout.visibility=View.VISIBLE
            EventBus.getDefault().postSticky(EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder(oankiGonderi!!.postID))
            var transaction=(activity as SearchActivity).supportFragmentManager.beginTransaction()
            transaction.hide( (activity as SearchActivity).supportFragmentManager.findFragmentByTag("fra2"))
            transaction.add(R.id.frameLayout,CommentFragment())
            transaction.addToBackStack("CommentFragment")
            transaction.commit()
        }
        //profile veya userprofile activity için geçerli
        else {
            (activity as AppCompatActivity).tumlayout.visibility= View.INVISIBLE
            (activity as AppCompatActivity).profileContainer.visibility=View.VISIBLE
            EventBus.getDefault().postSticky(EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder(oankiGonderi!!.postID))
            var transaction=(activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.hide( (activity as AppCompatActivity).supportFragmentManager.findFragmentByTag("fra2"))
            transaction.add(R.id.profileContainer,CommentFragment())
            transaction.addToBackStack("commentFragmentEklendi")
            transaction.commit()
        }






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
        Log.e("CCC","detach VIDEO DURDURULUR")
        myView!!.videoView.stopPlayback()
        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        Log.e("CCC","ON PAUSE CALISTI VIDEO DURDURULUR")
        myView!!.videoView.stopPlayback()
        super.onPause()
    }

    override fun onResume() {
        Log.e("CCC","ON RESUME CALISTI VIDEO DURDURULUR")
        myView!!.videoView.stopPlayback()

        super.onResume()
    }

}
