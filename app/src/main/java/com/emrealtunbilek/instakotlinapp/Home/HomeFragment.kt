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
import com.emrealtunbilek.instakotlinapp.VideoRecyclerView.view.CenterLayoutManager
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import com.emrealtunbilek.instakotlinapp.utils.HomeFragmentRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*


class HomeFragment : Fragment() {

    lateinit var fragmentView: View
    private val ACTIVITY_NO = 0

    lateinit var tumGonderiler: ArrayList<UserPosts>
    lateinit var sayfaBasinaTumGonderiler: ArrayList<UserPosts>
    lateinit var tumTakipEttiklerim: ArrayList<String>

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    var mRecyclerView: AutoPlayVideoRecyclerView? = null
    val SAYFA_BASINA_GONDERI_SAYISI = 10
    var sayfaNumarasi = 1
    var sayfaninSonunaGelindi = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentView = inflater?.inflate(R.layout.fragment_home, container, false)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList<UserPosts>()
        sayfaBasinaTumGonderiler = ArrayList<UserPosts>()
        tumTakipEttiklerim = ArrayList<String>()



        tumTakipEttiklerimiGetir()



        fragmentView.imgTabCamera.setOnClickListener {

            (activity as HomeActivity).homeViewPager.setCurrentItem(0)

        }

        fragmentView.imgTabDirectMessage.setOnClickListener {

            (activity as HomeActivity).homeViewPager.setCurrentItem(2)
        }

        fragmentView.refreshLayout.setOnRefreshListener {

            tumGonderiler.clear()
            sayfaBasinaTumGonderiler.clear()
            sayfaninSonunaGelindi = false
            kullaniciPostlariniGetir()
            fragmentView.refreshLayout.setRefreshing(false)
        }


        return fragmentView
    }

    private fun tumTakipEttiklerimiGetir() {

        tumTakipEttiklerim.add(mUser.uid)
        //Log.e("HATA9", "benim uidim ekleniyor..." + mUser.uid)

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.getValue() != null) {

                    for (ds in p0!!.children) {
                        tumTakipEttiklerim.add(ds.key)
                    }

                    //Log.e("HATA", "TÜM TAKIP ETTİKLERİM:" + tumTakipEttiklerim.toString())
                    kullaniciPostlariniGetir()

                } else {
                    //Log.e("HATA9", "hiç takip ettiğim yok,sadece kendi gönderilerimi görücem")
                    kullaniciPostlariniGetir()
                }


            }

        })

    }

    private fun kullaniciPostlariniGetir() {

        mRef = FirebaseDatabase.getInstance().reference
        //Log.e("ttt","takip edilecek user liste size:"+tumTakipEttiklerim.size+" liste:"+tumTakipEttiklerim)
        for (i in 0..tumTakipEttiklerim.size - 1) {

            var kullaniciID = tumTakipEttiklerim.get(i)


            mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if (p0!!.getValue() != null) {
                        var userID = kullaniciID
                        var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                        var kullaniciFotoURL = p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture


                        mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(p0: DataSnapshot?) {

                                if (p0!!.hasChildren()) {
                                    //Log.e("HATA", kullaniciID + " idli kişinin fotoları var")
                                    for (ds in p0!!.children) {

                                        var eklenecekUserPosts = UserPosts()
                                        eklenecekUserPosts.userID = userID
                                        eklenecekUserPosts.userName = kullaniciAdi
                                        eklenecekUserPosts.userPhotoURL = kullaniciFotoURL
                                        eklenecekUserPosts.postID = ds.getValue(Posts::class.java)!!.post_id
                                        eklenecekUserPosts.postURL = ds.getValue(Posts::class.java)!!.file_url
                                        eklenecekUserPosts.postAciklama = ds.getValue(Posts::class.java)!!.aciklama
                                        eklenecekUserPosts.postYuklenmeTarih = ds.getValue(Posts::class.java)!!.yuklenme_tarih

                                        tumGonderiler.add(eklenecekUserPosts)
                                        //Log.e("ttt","kullanıcının tüm gönderileri eklendi, diğer kullanıcıya geç")




                                    }
                                }
                                //Log.e("ttt","gönderiler eklenmiş, tüm kullanıcılar gezilmiş listeyi göster i:"+i+" size:"+tumGonderiler.size)
                                if (tumGonderiler.size>0 && i==(tumTakipEttiklerim.size - 1)){
                                    //Log.e("ttt1","gönderiler eklenmiş, tüm kullanıcılar gezilmiş listeyi göster i:"+i+" size:"+tumGonderiler.size)
                                    setupRecyclerView()
                                }



                            }

                        })
                    }else{
                        if (tumGonderiler.size>0 && i==(tumTakipEttiklerim.size - 1)){
                            //Log.e("ttt2","gönderiler eklenmiş, tüm kullanıcılar gezilmiş listeyi göster i:"+i+" size:"+tumGonderiler.size)
                            setupRecyclerView()
                        }
                    }


                }


            })


        }


    }

    private fun setupRecyclerView() {

        //Log.e("HATA", "3333333")
        Collections.sort(tumGonderiler, object : Comparator<UserPosts> {
            override fun compare(o1: UserPosts?, o2: UserPosts?): Int {
                if (o1!!.postYuklenmeTarih!! > o2!!.postYuklenmeTarih!!) {
                    return -1
                } else return 1
            }
        })

        if (tumGonderiler.size >= SAYFA_BASINA_GONDERI_SAYISI) {
            for (i in 0..SAYFA_BASINA_GONDERI_SAYISI - 1) {
                sayfaBasinaTumGonderiler.add(tumGonderiler.get(i))
            }
        } else {
            for (i in 0..tumGonderiler.size - 1) {
                sayfaBasinaTumGonderiler.add(tumGonderiler.get(i))
            }
        }


        //Log.e("XXX", "Tüm gönderi sayısı:" + tumGonderiler.size)
        //Log.e("XXX", "Sayfa basına düşen gönderi sayısı:" + sayfaBasinaTumGonderiler.size)

        mRecyclerView = fragmentView.recyclerview
        var recyclerAdapter = HomeFragmentRecyclerAdapter(this.activity!!, sayfaBasinaTumGonderiler)
        mRecyclerView!!.layoutManager = CenterLayoutManager(this.activity!!, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.adapter = recyclerAdapter


        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = mRecyclerView!!.layoutManager as CenterLayoutManager

                if (dy > 0 && layoutManager.findLastVisibleItemPosition() == mRecyclerView!!.adapter!!.itemCount - 1) {
                    //Log.e("XXX", "Liste sonuna gelindi, yeni öğeleri getirt")
                    //Log.e("XXX", "Son görülen öğenin pos:" + layoutManager.findLastVisibleItemPosition())
                    //Log.e("XXX", "Listedeki eleman sayısı:" + mRecyclerView!!.adapter!!.itemCount)
                    if (sayfaninSonunaGelindi == false)
                        listeyeYeniElemanlariEkle()
                }


            }

        })

    }

    private fun listeyeYeniElemanlariEkle() {

        if (mRecyclerView != null && mRecyclerView!!.getHandingVideoHolder() != null) {
            mRecyclerView!!.getHandingVideoHolder().stopVideo();
            //Log.e("HATA", "PAUSE CALISIYO")
        }

        var yeniGetirilecekElemanlarinAltSiniri = sayfaNumarasi * SAYFA_BASINA_GONDERI_SAYISI
        var yeniGetirilecekElemanlarinUstSiniri = (sayfaNumarasi + 1) * SAYFA_BASINA_GONDERI_SAYISI - 1
        for (i in yeniGetirilecekElemanlarinAltSiniri..yeniGetirilecekElemanlarinUstSiniri) {
            if (sayfaBasinaTumGonderiler.size <= tumGonderiler.size - 1) {
                sayfaBasinaTumGonderiler.add(tumGonderiler.get(i))
                mRecyclerView!!.adapter.notifyDataSetChanged()
            } else {
                sayfaninSonunaGelindi = true
                sayfaNumarasi = 0
                break
            }

        }
        //Log.e("XXX", "" + yeniGetirilecekElemanlarinAltSiniri + " dan " + yeniGetirilecekElemanlarinUstSiniri + " kadar eleman eklendi")
        sayfaNumarasi++
    }


    fun setupNavigationView() {

        var fragmentBottomNavView = fragmentView.bottomNavigationView

        BottomnavigationViewHelper.setupBottomNavigationView(fragmentBottomNavView)
        BottomnavigationViewHelper.setupNavigation(activity!!, fragmentBottomNavView, ACTIVITY_NO)
        var menu = fragmentBottomNavView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)

    }

    private fun setupAuthListener() {

        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {

                    //Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")

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
            //Log.e("HATA", "RESUME CALISIYO")
        }

    }

    override fun onPause() {
        super.onPause()
        sayfaNumarasi = 0
        if (mRecyclerView != null && mRecyclerView!!.getHandingVideoHolder() != null) {
            mRecyclerView!!.getHandingVideoHolder().stopVideo();
            //Log.e("HATA", "PAUSE CALISIYO")
        }
    }

    override fun onStart() {
        super.onStart()
        //Log.e("HATA", "HomeFragmenttesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }


}