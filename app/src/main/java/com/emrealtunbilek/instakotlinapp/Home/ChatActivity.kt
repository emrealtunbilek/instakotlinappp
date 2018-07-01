package com.emrealtunbilek.instakotlinapp.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.dinuscxj.refresh.RecyclerRefreshLayout
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
    lateinit var myRecyclerViewAdapter: MesajRecyclerViewAdapter
    lateinit var myRecyclerView: RecyclerView
    var sohbetEdilecekUser:Users? = null

    //sayfalamaiçin
    val SAYFA_BASI_GONDERI_SAYISI = 3
    var sayfaNumarasi=1

    var mesajPos=0
    var dahaFazlaMesajPos=0
    var ilkGetirilenMesajID=""

    lateinit var childEventListener:ChildEventListener
    lateinit var childListenerDahaFazla:ChildEventListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mRef=FirebaseDatabase.getInstance().reference

        sohbetEdilecekUserId=intent.getStringExtra("secilenUserID")
        mesajGonderenUserId=mAuth.currentUser!!.uid.toString()

        sohbetEdenlerinBilgileriniGetir(sohbetEdilecekUserId, mesajGonderenUserId)

        refreshLayout.setOnRefreshListener(object : RecyclerRefreshLayout.OnRefreshListener{
            override fun onRefresh() {

                sayfaNumarasi++
                dahaFazlaMesajPos=0

               // mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).removeEventListener(childEventListener)
                dahaFazlaMesajGetir()


            refreshLayout.setRefreshing(false)

            }

        })




        tvMesajGonderButton.setOnClickListener {

            var mesajText=etMesaj.text.toString()
            var mesajAtan=HashMap<String,Any>()
            mesajAtan.put("mesaj",mesajText)
            mesajAtan.put("goruldu",true)
            mesajAtan.put("time",ServerValue.TIMESTAMP)
            mesajAtan.put("type","text")
            mesajAtan.put("user_id",mesajGonderenUserId)

            var yeniMesajKey=mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).push().key
            mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).child(yeniMesajKey).setValue(mesajAtan)

            var mesajAlan=HashMap<String,Any>()
            mesajAlan.put("mesaj",mesajText)
            mesajAlan.put("goruldu",false)
            mesajAlan.put("time",ServerValue.TIMESTAMP)
            mesajAlan.put("type","text")
            mesajAlan.put("user_id",mesajGonderenUserId)
            mRef.child("mesajlar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).child(yeniMesajKey).setValue(mesajAlan)



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

    private fun dahaFazlaMesajGetir(){

      childListenerDahaFazla= mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId)
                                                    .orderByKey().endAt(ilkGetirilenMesajID).limitToLast(SAYFA_BASI_GONDERI_SAYISI)
                                                    .addChildEventListener(object : ChildEventListener{
                  override fun onCancelled(p0: DatabaseError?) {

                  }

                  override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

                  }

                  override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

                  }

                  override fun onChildAdded(p0: DataSnapshot?, p1: String?) {

                      var okunanMesaj=p0!!.getValue(Mesaj::class.java)
                      if(dahaFazlaMesajPos==0){

                          ilkGetirilenMesajID=p0!!.key

                      }
                      tumMesajlar.add(dahaFazlaMesajPos++,okunanMesaj!!)


                      myRecyclerViewAdapter.notifyDataSetChanged()
                      myRecyclerView.scrollToPosition(SAYFA_BASI_GONDERI_SAYISI)

                      Log.e("KONTROL","İLK OKUNAN MESAJ ID :"+ilkGetirilenMesajID)

                  }

                  override fun onChildRemoved(p0: DataSnapshot?) {

                  }

              })





    }

    private fun mesajlariGetir() {



       /* mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).addValueEventListener(object : ValueEventListener{
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


        })*/

         childEventListener= mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).limitToLast(sayfaNumarasi * SAYFA_BASI_GONDERI_SAYISI).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {

                var okunanMesaj=p0!!.getValue(Mesaj::class.java)
                tumMesajlar.add(okunanMesaj!!)

                if(mesajPos==0){

                    ilkGetirilenMesajID=p0!!.key

                }
                mesajPos++



                myRecyclerViewAdapter.notifyDataSetChanged()
                myRecyclerView.scrollToPosition(tumMesajlar.size-1)

                Log.e("KONTROL","İLK OKUNAN MESAJ ID :"+ilkGetirilenMesajID)


            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }

        })


    }

    private fun setupMesajlarRecyclerView() {
        var myLinearLayoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        myLinearLayoutManager.stackFromEnd=true
        myRecyclerView=rvSohbet

        myRecyclerViewAdapter=MesajRecyclerViewAdapter(tumMesajlar, this, sohbetEdilecekUser!!)
        myRecyclerView.layoutManager=myLinearLayoutManager
        myRecyclerView.adapter=myRecyclerViewAdapter

        mesajlariGetir()

    }

    private fun sohbetEdenlerinBilgileriniGetir(sohbetEdilecekUserId: String?, oturumAcanUserID:String?) {

        mRef.child("users").child(sohbetEdilecekUserId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.getValue() != null){

                    sohbetEdilecekUser=p0!!.getValue(Users::class.java)
                    tvSohbetEdilecekUserName.setText(sohbetEdilecekUser!!.user_name!!.toString())

                    setupMesajlarRecyclerView()
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
