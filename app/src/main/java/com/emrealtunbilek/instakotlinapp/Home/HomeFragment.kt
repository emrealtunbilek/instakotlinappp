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
import com.emrealtunbilek.instakotlinapp.Models.Posts
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import com.emrealtunbilek.instakotlinapp.utils.HomeFragmentRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    lateinit var fragmentView: View
    private val ACTIVITY_NO = 0

    lateinit var tumGonderiler: ArrayList<UserPosts>
    lateinit var tumTakipEttiklerim:ArrayList<String>

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    var mRecyclerView: AutoPlayVideoRecyclerView?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentView = inflater?.inflate(R.layout.fragment_home, container, false)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList<UserPosts>()
        tumTakipEttiklerim=ArrayList<String>()



        tumTakipEttiklerimiGetir()



        fragmentView.imgTabCamera.setOnClickListener {

            (activity as HomeActivity).homeViewPager.setCurrentItem(0)

        }

        fragmentView.imgTabDirectMessage.setOnClickListener {

            (activity as HomeActivity).homeViewPager.setCurrentItem(2)
        }


        return fragmentView
    }

    private fun tumTakipEttiklerimiGetir() {

        tumTakipEttiklerim.add(mUser.uid)
        Log.e("HATA9","benim uidim ekleniyor..."+mUser.uid)

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.getValue()!=null){

                    for(ds in p0!!.children){
                        tumTakipEttiklerim.add(ds.key)
                    }

                    Log.e("HATA","TÜM TAKIP ETTİKLERİM:"+tumTakipEttiklerim.toString())
                    kullaniciPostlariniGetir()

                }else {
                    Log.e("HATA9","hiç takip ettiğim yok,sadece kendi gönderilerimi görücem")
                    kullaniciPostlariniGetir()
                }


            }

        })

    }

    private fun kullaniciPostlariniGetir() {

        mRef=FirebaseDatabase.getInstance().reference

        for (i in 0..tumTakipEttiklerim.size-1){

            var kullaniciID=tumTakipEttiklerim.get(i)
            Log.e("HATA9",kullaniciID+" idli kullanıcı resimleri getiriliyor")

            mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    var userID = kullaniciID
                    var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                    var kullaniciFotoURL=p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture


                    mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {

                            if(p0!!.hasChildren())
                            {
                                Log.e("HATA",kullaniciID+" idli kişinin fotoları var")
                                for (ds in p0!!.children){

                                    var eklenecekUserPosts=UserPosts()
                                    eklenecekUserPosts.userID=userID
                                    eklenecekUserPosts.userName=kullaniciAdi
                                    eklenecekUserPosts.userPhotoURL=kullaniciFotoURL
                                    eklenecekUserPosts.postID=ds.getValue(Posts::class.java)!!.post_id
                                    eklenecekUserPosts.postURL=ds.getValue(Posts::class.java)!!.file_url
                                    eklenecekUserPosts.postAciklama=ds.getValue(Posts::class.java)!!.aciklama
                                    eklenecekUserPosts.postYuklenmeTarih=ds.getValue(Posts::class.java)!!.yuklenme_tarih

                                    tumGonderiler.add(eklenecekUserPosts)

                                }
                            }else {
                                Log.e("HATA",kullaniciID+" idli kişinin fotoları yok")
                            }

                            Log.e("HATA",kullaniciID+" idli kişinin fotoları var, sayisi:"+tumGonderiler.size)

                            if(i >= tumTakipEttiklerim.size-1)
                            setupRecyclerView()

                        }

                    })




                }


            })



        }






    }

    private fun setupRecyclerView() {


        mRecyclerView=fragmentView.recyclerview
        var recyclerAdapter=HomeFragmentRecyclerAdapter(this.activity!!,tumGonderiler)

        mRecyclerView!!.adapter=recyclerAdapter

        mRecyclerView!!.layoutManager=LinearLayoutManager(this.activity!!,LinearLayoutManager.VERTICAL,false)
    }


    fun setupNavigationView() {

        var fragmentBottomNavView = fragmentView.bottomNavigationView

        BottomnavigationViewHelper.setupBottomNavigationView(fragmentBottomNavView)
        BottomnavigationViewHelper.setupNavigation(activity!!, fragmentBottomNavView,ACTIVITY_NO)
        var menu = fragmentBottomNavView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)

    }

    private fun setupAuthListener() {

        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {

                    Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")

                    var intent = Intent(activity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish()
                } else {


                }
            }

        }
    }


    override fun onResume() {
        setupNavigationView()
        super.onResume()
        if (mRecyclerView != null && mRecyclerView?.getHandingVideoHolder() != null) {
            mRecyclerView!!.getHandingVideoHolder().playVideo();
            Log.e("HATA","RESUME CALISIYO")
        }

    }

    override fun onPause() {
        super.onPause()
        if (mRecyclerView != null && mRecyclerView!!.getHandingVideoHolder() != null){
            mRecyclerView!!.getHandingVideoHolder().stopVideo();
            Log.e("HATA","PAUSE CALISIYO")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("HATA", "HomeFragmenttesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }


}