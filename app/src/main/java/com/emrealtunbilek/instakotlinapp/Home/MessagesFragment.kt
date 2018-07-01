package com.emrealtunbilek.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.Models.Konusmalar
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.KonusmalarRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_messages.view.*

/**
 * Created by Emre on 28.02.2018.
 */
class MessagesFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var tumKonusmalar:ArrayList<Konusmalar> = ArrayList<Konusmalar>()
    lateinit var myRecyclerView:RecyclerView
    lateinit var myLinearLayoutManager: LinearLayoutManager
    lateinit var myAdapter:KonusmalarRecyclerAdapter
    lateinit var myFragmentView:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        myFragmentView=inflater?.inflate(R.layout.fragment_messages, container, false)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()


        myFragmentView.searchview.setOnClickListener {

            var intent=Intent(activity,AlgolisSearchMesajActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)

        }
        tumKonusmalariGetir()


        return myFragmentView
    }

    private fun tumKonusmalariGetir() {

        var mRef=FirebaseDatabase.getInstance().reference
        var mUser=FirebaseAuth.getInstance().currentUser
        mRef.child("konusmalar").child(mUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                if(p0!!.getValue() != null){

                    for(konusma in p0!!.children){

                        var okunanKonusma = konusma.getValue(Konusmalar::class.java)
                        okunanKonusma!!.user_id=konusma.key
                        tumKonusmalar.add(okunanKonusma!!)


                    }

                    setupKonusmalarRecylerView()

                }


            }


        })


    }

    private fun setupKonusmalarRecylerView() {

        myRecyclerView=myFragmentView.recyclerKonusmalar
        myLinearLayoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        myAdapter=KonusmalarRecyclerAdapter(tumKonusmalar,this!!.activity!!)

        myRecyclerView.layoutManager=myLinearLayoutManager
        myRecyclerView.adapter=myAdapter



    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    Log.e("HATA", "Kullanıcı oturum açmamış, HomeActivitydesn")
                    var intent = Intent(activity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish()
                } else {


                }
            }

        }
    }
}