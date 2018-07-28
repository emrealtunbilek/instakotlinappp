package com.emrealtunbilek.instakotlinapp.News


import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinuscxj.refresh.RecyclerRefreshLayout
import com.emrealtunbilek.instakotlinapp.Models.BildirimModel

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.MaterialRefreshView
import com.emrealtunbilek.instakotlinapp.utils.SenNewsRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_takip_sen.view.*


class SenNewsFragment : Fragment() {

    lateinit var myView:View
    var benimTumBildirimlerim=ArrayList<BildirimModel>()
    lateinit var myRecyclerView: RecyclerView
    lateinit var myLinearLayoutManager: LinearLayoutManager
    lateinit var myRecyclerAdapter:SenNewsRecyclerAdapter
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        myView=inflater.inflate(R.layout.fragment_takip_sen, container, false)

        mAuth=FirebaseAuth.getInstance()
        mRef=FirebaseDatabase.getInstance().reference


        tumBildirimlerimiGetir()


        myView.refreshLayout.setRefreshView(MaterialRefreshView(activity),ViewGroup.LayoutParams(100,100))

        myView.refreshLayout.setOnRefreshListener(object : RecyclerRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                benimTumBildirimlerim.clear()
                myRecyclerAdapter.notifyDataSetChanged()
                tumBildirimlerimiGetir()
                myView.refreshLayout.setRefreshing(false)
            }

        })







        return myView
    }

    private fun tumBildirimlerimiGetir() {

        myView.progressBar.visibility=View.VISIBLE
        myView.newsSenRecyclerview.visibility=View.INVISIBLE

        mRef.child("benim_bildirimlerim").child(mAuth.currentUser!!.uid).orderByChild("time").limitToLast(100).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.getValue() !=null){
                    for(bildirim in p0!!.children){

                        var okunanBildirim=bildirim.getValue(BildirimModel::class.java)
                        benimTumBildirimlerim.add(okunanBildirim!!)

                    }

                    setupRecyclerView()

                }else{
                    myView.progressBar.visibility=View.GONE
                    setupRecyclerView()
                }
            }


        })





    }

    private fun setupRecyclerView() {
        myRecyclerView=myView.newsSenRecyclerview
        myRecyclerAdapter=SenNewsRecyclerAdapter(activity!!,benimTumBildirimlerim)

        myLinearLayoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        myRecyclerView.layoutManager=myLinearLayoutManager

        myRecyclerView.adapter=myRecyclerAdapter

       object : CountDownTimer(1000,1000){
            override fun onFinish() {
                myView.progressBar.visibility=View.GONE
                myView.newsSenRecyclerview.visibility=View.VISIBLE
            }

            override fun onTick(p0: Long) {

            }

        }.start()




    }

}
