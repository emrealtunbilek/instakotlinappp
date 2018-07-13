package com.emrealtunbilek.instakotlinapp.News


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.BildirimModel

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.TakipNewsRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_takip_news.view.*


class TakipNewsFragment : Fragment() {

    lateinit var myView:View


    var takipEttikleriminTumBildirimleri =ArrayList<BildirimModel>()


    lateinit var myRecyclerView: RecyclerView
    lateinit var myLinearLayoutManager: LinearLayoutManager
    lateinit var myRecyclerAdapter: TakipNewsRecyclerAdapter
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        myView=inflater.inflate(R.layout.fragment_takip_news, container, false)

        mRef=FirebaseDatabase.getInstance().reference
        mAuth= FirebaseAuth.getInstance()

        takipEttigimKullanicilariGetir()



        return myView
    }

    private fun takipEttigimKullanicilariGetir() {
        mRef.child("following").child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                for(userID in p0!!.children){

                    var takipEttigimUserID=userID.key

                    mRef.child("takip_ettiklerimin_bildirimleri").child(takipEttigimUserID).limitToLast(10).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if(p0!!.getValue() != null){

                                for (bildirim in p0!!.children){

                                    var takipEttigimUser=takipEttigimUserID
                                    var takipEttigimUserBildirimi=bildirim.getValue(BildirimModel::class.java)
                                    takipEttigimUserBildirimi!!.takip_ettigimin_user_id=takipEttigimUser

                                    takipEttikleriminTumBildirimleri.add(takipEttigimUserBildirimi)

                                }


                                listeyiHazirla()


                            }
                        }


                    })

                }

            }


        })
    }

    private fun listeyiHazirla() {

        myRecyclerView=myView.takipEttikleriminBildirimListesi
        myLinearLayoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)

        myRecyclerView.layoutManager=myLinearLayoutManager
        myRecyclerAdapter=TakipNewsRecyclerAdapter(activity!!,takipEttikleriminTumBildirimleri)

        myRecyclerView.adapter=myRecyclerAdapter



    }

}
