package com.emrealtunbilek.instakotlinapp.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.R
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

        tvMesajGonderButton.setOnClickListener {

            var mesaj=HashMap<String,Any>()
            mesaj.put("mesaj",etMesaj.text.toString())
            mesaj.put("goruldu",false)
            mesaj.put("time",ServerValue.TIMESTAMP)
            mesaj.put("type","text")

            mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).push().setValue(mesaj)
            mRef.child("mesajlar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).push().setValue(mesaj)



            var konusma=HashMap<String,Any>()
            konusma.put("time",ServerValue.TIMESTAMP)
            konusma.put("goruldu",false)

            mRef.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).setValue(konusma)
            mRef.child("konusmalar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).setValue(konusma)


            etMesaj.setText("")



        }

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
