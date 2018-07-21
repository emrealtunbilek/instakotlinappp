package com.emrealtunbilek.instakotlinapp.Profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.Posts
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.Models.Users

import com.emrealtunbilek.instakotlinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BegendigimGonderilerFragment : Fragment() {

    var myView: View? = null
    var begendigimTumGonderiler = ArrayList<UserPosts>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_begendigim_gonderiler, container, false)

        begendigimGonderileriGetir()

        return myView
    }

    private fun begendigimGonderileriGetir() {

        var userID = FirebaseAuth.getInstance().currentUser!!.uid
        var mRef = FirebaseDatabase.getInstance().reference
        var begendigimGonderiSayisi=0

        mRef.child("likes").orderByChild(userID).equalTo(userID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }
            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.getValue()!=null) {

                    begendigimGonderiSayisi=p0!!.childrenCount.toInt()
                    for(begenilenGonderi in p0!!.children){
                        var begendigimGonderiID= begenilenGonderi.key!!.toString()
                        Log.e("CCC","begnedigim gönderi id:"+begendigimGonderiID)

                        mRef.child("posts").orderByChild(begendigimGonderiID).limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                                if(p0!!.getValue()!=null){

                                    for(post in p0!!.children){

                                        var gonderiyiPaylasanUserID=post!!.key
                                        var gonderi=post.child(begendigimGonderiID).getValue(Posts::class.java)!!
                                        Log.e("CCC","BEGENDİĞİM GÖNDERİ:"+gonderi!!.toString())

                                        mRef.child("users").child(gonderiyiPaylasanUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                                            override fun onCancelled(p0: DatabaseError?) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                var begendigimPost= UserPosts()
                                                begendigimPost.userID=gonderiyiPaylasanUserID
                                                begendigimPost.userName=p0!!.getValue(Users::class.java)!!.user_name!!.toString()
                                                begendigimPost.userPhotoURL=p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture!!.toString()
                                                begendigimPost.postID=gonderi.post_id
                                                begendigimPost.postURL=gonderi.file_url
                                                begendigimPost.postAciklama=gonderi.aciklama
                                                begendigimPost.postYuklenmeTarih=gonderi.yuklenme_tarih

                                                begendigimTumGonderiler.add(begendigimPost)
                                                Log.e("CCC","SON :"+begendigimPost!!.toString())
                                                if(begendigimGonderiSayisi==begendigimTumGonderiler.size){
                                                    listeyiHazirla()
                                                }
                                            }

                                        })



                                    }



                                }
                            }

                        })



                    }



                }
            }

        })

    }

    private fun listeyiHazirla() {
        Log.e("CCC","SON :"+begendigimTumGonderiler.size)
    }
}
