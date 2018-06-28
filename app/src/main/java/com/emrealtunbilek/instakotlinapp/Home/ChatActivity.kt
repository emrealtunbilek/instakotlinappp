package com.emrealtunbilek.instakotlinapp.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.Models.Mesaj
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.MesajRecyclerViewAdapter
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var sohbetEdilecekUserId:String
    lateinit var mesajGonderenUserId:String
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef:DatabaseReference
    var tumMesajlar:ArrayList<Mesaj> = ArrayList<Mesaj>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mRef=FirebaseDatabase.getInstance().reference

        sohbetEdilecekUserId=intent.getStringExtra("secilenUserID")
        mesajGonderenUserId=mAuth.currentUser!!.uid.toString()

        sohbetEdenlerinBilgileriniGetir(sohbetEdilecekUserId, mesajGonderenUserId)

        Toast.makeText(this,"Secilen id :"+sohbetEdilecekUserId,Toast.LENGTH_SHORT).show()

        mesajlariGetir()

        tvMesajGonderButton.setOnClickListener {

            var mesajText=etMesaj.text.toString()
            var mesajAtan=HashMap<String,Any>()
            mesajAtan.put("mesaj",mesajText)
            mesajAtan.put("goruldu",true)
            mesajAtan.put("time",ServerValue.TIMESTAMP)
            mesajAtan.put("type","text")
            mesajAtan.put("user_id",mesajGonderenUserId)

            mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).push().setValue(mesajAtan)

            var mesajAlan=HashMap<String,Any>()
            mesajAlan.put("mesaj",mesajText)
            mesajAlan.put("goruldu",false)
            mesajAlan.put("time",ServerValue.TIMESTAMP)
            mesajAlan.put("type","text")
            mesajAlan.put("user_id",mesajGonderenUserId)
            mRef.child("mesajlar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).push().setValue(mesajAlan)



            var konusmaMesajAtan=HashMap<String,Any>()
            konusmaMesajAtan.put("time",ServerValue.TIMESTAMP)
            konusmaMesajAtan.put("goruldu",true)
            konusmaMesajAtan.put("son_mesaj",mesajText)

            mRef.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).setValue(konusmaMesajAtan)

            var konusmaMesajAlan=HashMap<String,Any>()
            konusmaMesajAlan.put("time",ServerValue.TIMESTAMP)
            konusmaMesajAlan.put("goruldu",false)
            konusmaMesajAlan.put("son_mesaj",mesajText)


            mRef.child("konusmalar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).setValue(konusmaMesajAlan)


            etMesaj.setText("")



        }

    }

    private fun mesajlariGetir() {



        mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                tumMesajlar.clear()

                if(p0!!.getValue() != null){

                    for (mesaj in p0!!.children){

                        var okunanMesaj=mesaj.getValue(Mesaj::class.java)
                        tumMesajlar.add(okunanMesaj!!)

                    }

                    setupMesajlarRecyclerView()


                }

            }


        })


    }

    private fun setupMesajlarRecyclerView() {
        var myLinearLayoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        myLinearLayoutManager.stackFromEnd=true
        var myRecyclerview=rvSohbet

        var myAdapter=MesajRecyclerViewAdapter(tumMesajlar, this)
        myRecyclerview.layoutManager=myLinearLayoutManager
        myRecyclerview.adapter=myAdapter

    }

    private fun sohbetEdenlerinBilgileriniGetir(sohbetEdilecekUserId: String?, oturumAcanUserID:String?) {

        mRef.child("users").child(sohbetEdilecekUserId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.getValue() != null){

                    var bulunanKullanici=p0!!.getValue(Users::class.java)
                    tvSohbetEdilecekUserName.setText(bulunanKullanici!!.user_name!!.toString())
                }
            }


        })

        mRef.child("users").child(oturumAcanUserID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.getValue() != null){

                    var bulunanKullanici=p0!!.getValue(Users::class.java)
                    var imgUrl=bulunanKullanici!!.user_detail!!.profile_picture.toString()
                    UniversalImageLoader.setImage(imgUrl,circleImageView,null,"")
                }
            }


        })


    }


    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    Log.e("HATA", "Kullanıcı oturum açmamış, HomeActivitydesn")
                    var intent = Intent(this@ChatActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {


                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("HATA", "HomeActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
