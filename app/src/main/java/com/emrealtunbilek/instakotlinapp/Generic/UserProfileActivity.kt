package com.emrealtunbilek.instakotlinapp.Generic

import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.Models.Posts
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.Profile.ProfileEditFragment

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.VideoRecyclerView.view.CenterLayoutManager
import com.emrealtunbilek.instakotlinapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.greenrobot.eventbus.EventBus

class UserProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    private val TAG = "UserProfileActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var tumGonderiler: ArrayList<UserPosts>
    lateinit var secilenUserID: String
    var kullaniciPostListe: AutoPlayVideoRecyclerView? = null

    var profilGizliMi = false
    var takipEdiyorMuyum = false
    var listenerAtandiMi = false

    var ilkAcilis=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        secilenUserID = intent.getStringExtra("secilenUserID")
        tumGonderiler = ArrayList<UserPosts>()


        setupButtons()
        takipciSayilariniGuncelle()



    }

    private fun takibeUygunsaGoster() {

        if (profilGizliMi == false || (profilGizliMi == true && takipEdiyorMuyum == true)) {

            golgelik2.visibility = View.VISIBLE
            golgelik3.visibility = View.VISIBLE
            containerButtons.visibility = View.VISIBLE
            profileRecyclerView.visibility = View.VISIBLE
            containerGizliUyari.visibility = View.GONE

            tumGonderiler.clear()
            kullaniciPostlariniGetir(secilenUserID, 1)

            imgGrid.setOnClickListener {
                tumGonderiler.clear()
                kullaniciPostlariniGetir(secilenUserID, 1)

            }

            imgList.setOnClickListener {
                tumGonderiler.clear()
                kullaniciPostlariniGetir(secilenUserID, 2)
            }

        } else {


            if (kullaniciPostListe != null && kullaniciPostListe!!.getHandingVideoHolder() != null) {
                kullaniciPostListe!!.getHandingVideoHolder().stopVideo();
                Log.e("HATA", "PAUSE CALISIYO")
            }

            tumGonderiler.clear()

            golgelik2.visibility = View.GONE
            golgelik3.visibility = View.GONE
            containerButtons.visibility = View.GONE
            profileRecyclerView.visibility = View.GONE
            containerGizliUyari.visibility = View.VISIBLE


        }

    }

    private fun kullaniciBilgileriniGetir() {

        tvTakip.isEnabled = false
        imgProfileSettings.isEnabled = false

        if(listenerAtandiMi==false){
            listenerAtandiMi=true
            mRef.child("users").child(secilenUserID).addValueEventListener(myListener)
        }


    }

    private var myListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(p0: DataSnapshot?) {

            if (p0!!.getValue() != null) {
                var okunanKullaniciBilgileri = p0!!.getValue(Users::class.java)


                EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonder(okunanKullaniciBilgileri))
                tvTakip.isEnabled = true
                imgProfileSettings.isEnabled = true

                if (p0!!.child("gizli_profil").getValue() != null) {
                    profilGizliMi = p0!!.child("gizli_profil").getValue().toString().toBoolean()
                } else {
                    profilGizliMi = false
                }

                tvProfilAdiToolbar.setText(okunanKullaniciBilgileri!!.user_name)
                tvProfilGercekAdi.setText(okunanKullaniciBilgileri!!.adi_soyadi)
                tvFollowerSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.follower)
                tvFollowingSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.following)
                tvPostSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.post)

                if(ilkAcilis){
                    ilkAcilis=false
                    var imgUrl: String = okunanKullaniciBilgileri!!.user_detail!!.profile_picture!!
                    UniversalImageLoader.setImage(imgUrl, circleProfileImage, progressBar, "")
                }


                if (!okunanKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()) {
                    tvBiyografi.visibility = View.VISIBLE
                    tvBiyografi.setText(okunanKullaniciBilgileri!!.user_detail!!.biography!!)
                } else {
                    tvBiyografi.visibility = View.GONE
                }
                if (!okunanKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()) {
                    tvWebSitesi.visibility = View.VISIBLE
                    tvWebSitesi.setText(okunanKullaniciBilgileri!!.user_detail!!.web_site!!)
                } else {
                    tvWebSitesi.visibility = View.GONE
                }

            }


            takipBilgisiniGetir()


        }


    }

    private fun takipBilgisiniGetir() {

        if(profilGizliMi==false){

            mRef.child("takip_istekleri").child(secilenUserID).child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if(p0!!.getValue()!=null){
                        mRef.child("takip_istekleri").child(secilenUserID).child(mUser.uid).removeValue()
                        Bildirimler.bildirimKaydet(secilenUserID,Bildirimler.TAKIP_ISTEGINI_SIL)


                        mRef.child("following").child(mUser.uid).child(secilenUserID).setValue(secilenUserID)
                        mRef.child("follower").child(secilenUserID).child(mUser.uid).setValue(mUser.uid)

                        Log.e("KONTROL", "PROFILE ARTIK GIZLI DEGIL TAKIBE BASLA")

                        Bildirimler.bildirimKaydet(secilenUserID,Bildirimler.TAKIP_ETMEYE_BASLADI)

                        takipciSayilariniGuncelle()
                        takibiBirakButonOzellikleri()
                    }
                }

            })

        }



        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.hasChild(secilenUserID)) {
                    takipEdiyorMuyum = true
                    takibiBirakButonOzellikleri()
                } else {
                    takipEdiyorMuyum = false
                    takipEtButonOzellikleri()
                    FirebaseDatabase.getInstance().getReference().child("takip_istekleri").child(secilenUserID)
                            .child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.getValue() != null) {
                                istekGonderildiButonOzellikleri()
                            }
                        }


                    })
                }
                takibeUygunsaGoster()
            }


        })
    }

    fun takipEtButonOzellikleri() {
        tvTakip.setText("Takip Et")
        tvTakip.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.beyaz))
        tvTakip.setBackgroundResource(R.drawable.register_button_aktif)
    }

    fun takibiBirakButonOzellikleri() {
        tvTakip.setText("Takipi Bırak")
        tvTakip.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.siyah))
        tvTakip.setBackgroundResource(R.drawable.takip_et_beyaz)
    }


    private fun setupButtons() {
        imgProfileSettings.setOnClickListener {

        }

        imgBack.setOnClickListener {

            onBackPressed()
        }

        tvTakip.setOnClickListener {


            mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.hasChild(secilenUserID)) {
                        mRef.child("following").child(mUser.uid).child(secilenUserID).removeValue()
                        mRef.child("follower").child(secilenUserID).child(mUser.uid).removeValue()

                        Bildirimler.bildirimKaydet(secilenUserID,Bildirimler.TAKIP_ETMEYI_BIRAKTI)

                        takipciSayilariniGuncelle()
                        takipEtButonOzellikleri()

                    } else {


                        if (profilGizliMi == true) {

                            mRef.child("takip_istekleri").child(secilenUserID).child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError?) {

                                }

                                override fun onDataChange(p0: DataSnapshot?) {

                                    if(p0!!.getValue() != null){

                                        mRef.child("takip_istekleri").child(secilenUserID).child(mUser.uid).removeValue()
                                        Bildirimler.bildirimKaydet(secilenUserID,Bildirimler.TAKIP_ISTEGINI_SIL)
                                        takipEtButonOzellikleri()

                                    }else {
                                        mRef.child("takip_istekleri").child(secilenUserID).child(mUser.uid).setValue(mUser.uid)

                                        Bildirimler.bildirimKaydet(secilenUserID, Bildirimler.YENI_TAKIP_ISTEGI)

                                        istekGonderildiButonOzellikleri()
                                    }


                                }

                            })

                        } else {


                            mRef.child("following").child(mUser.uid).child(secilenUserID).setValue(secilenUserID)
                            mRef.child("follower").child(secilenUserID).child(mUser.uid).setValue(mUser.uid)


                            Bildirimler.bildirimKaydet(secilenUserID,Bildirimler.TAKIP_ETMEYE_BASLADI)

                            takipciSayilariniGuncelle()
                            takibiBirakButonOzellikleri()
                        }


                    }
                }


            })

        }


    }


    private fun istekGonderildiButonOzellikleri() {
        tvTakip.setText("ISTEK GONDERILDI")
        tvTakip.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.siyah))
        tvTakip.setBackgroundResource(R.drawable.takip_et_beyaz)
    }

    private fun takipciSayilariniGuncelle() {

        mRef = FirebaseDatabase.getInstance().reference

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                var takipEttikleriminSayisi = p0!!.childrenCount.toString()

                mRef.child("follower").child(secilenUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        var takipEdenlerinSayisi = p0!!.childrenCount.toString()

                        mRef.child("users").child(mUser.uid).child("user_detail").child("following").setValue(takipEttikleriminSayisi)
                        mRef.child("users").child(secilenUserID).child("user_detail").child("follower").setValue(takipEdenlerinSayisi)


                        kullaniciBilgileriniGetir()
                    }

                })
            }

        })

    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
        if (kullaniciPostListe != null && kullaniciPostListe?.getHandingVideoHolder() != null) {
            kullaniciPostListe!!.getHandingVideoHolder().playVideo();
            Log.e("HATA", "RESUME CALISIYO")
        }

    }

    override fun onPause() {
        super.onPause()
        if (kullaniciPostListe != null && kullaniciPostListe!!.getHandingVideoHolder() != null) {
            kullaniciPostListe!!.getHandingVideoHolder().stopVideo();
            Log.e("HATA", "PAUSE CALISIYO")
        }
    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun kullaniciPostlariniGetir(kullaniciID: String, layoutCesidi: Int) {


        mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                var userID = kullaniciID
                var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                var kullaniciFotoURL = p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture


                mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        if (p0!!.hasChildren()) {
                            Log.e("HATA", "COCUK VAR")
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

                            }
                        }

                        setupRecyclerView(layoutCesidi)

                    }

                })


            }


        })


    }

    //1 ise grid 2 ise list view şeklinde veriler gösterilir
    private fun setupRecyclerView(layoutCesidi: Int) {

        if (layoutCesidi == 1) {

            if (kullaniciPostListe != null && kullaniciPostListe!!.getHandingVideoHolder() != null) {
                kullaniciPostListe!!.getHandingVideoHolder().stopVideo();
                Log.e("HATA", "Gridlayout aktif, videoları durdur")
            }

            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)
            kullaniciPostListe = profileRecyclerView
            kullaniciPostListe?.layoutManager = GridLayoutManager(this, 3)
            kullaniciPostListe?.adapter = ProfilePostGridRecyclerAdapter(tumGonderiler, this)


        } else if (layoutCesidi == 2) {
            if (kullaniciPostListe != null && kullaniciPostListe?.getHandingVideoHolder() != null) {
                kullaniciPostListe!!.getHandingVideoHolder().playVideo();
                Log.e("HATA", "Listview aktif, varsa bekleyen videoyu oynat")
            }

            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            kullaniciPostListe = profileRecyclerView
            kullaniciPostListe?.layoutManager = CenterLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            kullaniciPostListe?.adapter = ProfilePostListRecyclerAdapter(this, tumGonderiler)


        }

    }

    override fun onBackPressed() {

        super.onBackPressed()
    }

    private fun setupAuthListener() {


        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {

                    Log.e("HATA", "Kullanıcı oturum açmamış, UserProfileActivitydesin")

                    var intent = Intent(this@UserProfileActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
        Log.e("HATA", "UserProfileActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
