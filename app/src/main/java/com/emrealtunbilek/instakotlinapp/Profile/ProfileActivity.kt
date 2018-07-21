package com.emrealtunbilek.instakotlinapp.Profile

import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.emrealtunbilek.instakotlinapp.Generic.TekGonderiFragment
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.Models.Posts
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.VideoRecyclerView.view.CenterLayoutManager
import com.emrealtunbilek.instakotlinapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList


class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO=4
    private val TAG="ProfileActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser:FirebaseUser
    lateinit var mRef:DatabaseReference
    lateinit var tumGonderiler: ArrayList<UserPosts>
    var myRecyclerView: AutoPlayVideoRecyclerView?=null

    var ilkAcilis=true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference

        tumGonderiler=ArrayList<UserPosts>()
        setupToolbar()


        takipciSayilariniGuncelle()



        kullaniciPostlariniGetir(mUser.uid)

        imgGrid.setOnClickListener {

            setupRecyclerView(1)

        }

        imgList.setOnClickListener {

            setupRecyclerView(2)
        }

    }

    private fun kullaniciBilgileriniGetir() {

        tvProfilDuzenle.isEnabled=false
        imgProfileSettings.isEnabled=false

        mRef.child("users").child(mUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                if(p0!!.getValue() != null){
                    var okunanKullaniciBilgileri=p0!!.getValue(Users::class.java)


                    EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonder(okunanKullaniciBilgileri))
                    tvProfilDuzenle.isEnabled=true
                    imgProfileSettings.isEnabled=true

                    tvProfilAdiToolbar.setText(okunanKullaniciBilgileri!!.user_name)
                    tvProfilGercekAdi.setText(okunanKullaniciBilgileri!!.adi_soyadi)
                    tvFollowerSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.follower)
                    tvFollowingSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.following)
                    tvPostSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.post)

                    if (ilkAcilis){
                        ilkAcilis=false
                        var imgUrl:String=okunanKullaniciBilgileri!!.user_detail!!.profile_picture!!
                        UniversalImageLoader.setImage(imgUrl,circleProfileImage,progressBar,"")
                    }


                    if(!okunanKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()){
                        tvBiyografi.visibility=View.VISIBLE
                        tvBiyografi.setText(okunanKullaniciBilgileri!!.user_detail!!.biography!!)
                    }else{
                        tvBiyografi.visibility=View.GONE
                    }
                    if(!okunanKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()){
                        tvWebSitesi.visibility=View.VISIBLE
                        tvWebSitesi.setText(okunanKullaniciBilgileri!!.user_detail!!.web_site!!)
                    }else{
                        tvWebSitesi.visibility=View.GONE
                    }

                }



            }


        })

    }
    

    private fun setupToolbar() {
       imgProfileSettings.setOnClickListener {
           var intent=Intent(this,ProfileSettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
           startActivity(intent)


       }

       tvProfilDuzenle.setOnClickListener {

           tumlayout.visibility= View.INVISIBLE
           profileContainer.visibility=View.VISIBLE
           var transaction=supportFragmentManager.beginTransaction()
           transaction.replace(R.id.profileContainer,ProfileEditFragment())
           transaction.addToBackStack("editProfileFragmentEklendi")
           transaction.commit()

       }

    }


    private fun takipciSayilariniGuncelle() {

        mRef = FirebaseDatabase.getInstance().reference

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                var takipEttikleriminSayisi = p0!!.childrenCount.toString()

                mRef.child("follower").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        var takipEdenlerinSayisi = p0!!.childrenCount.toString()

                        mRef.child("users").child(mUser.uid).child("user_detail").child("following").setValue(takipEttikleriminSayisi)
                        mRef.child("users").child(mUser.uid).child("user_detail").child("follower").setValue(takipEdenlerinSayisi)


                        kullaniciBilgileriniGetir()
                    }

                })
            }

        })

    }

    fun setupNavigationView(){

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView,ACTIVITY_NO)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun kullaniciPostlariniGetir(kullaniciID: String) {



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
                            Log.e("HATA","COCUK VAR")
                            for (ds in p0!!.children){

                                var eklenecekUserPosts= UserPosts()
                                eklenecekUserPosts.userID=userID
                                eklenecekUserPosts.userName=kullaniciAdi
                                eklenecekUserPosts.userPhotoURL=kullaniciFotoURL
                                eklenecekUserPosts.postID=ds.getValue(Posts::class.java)!!.post_id
                                eklenecekUserPosts.postURL=ds.getValue(Posts::class.java)!!.file_url
                                eklenecekUserPosts.postAciklama=ds.getValue(Posts::class.java)!!.aciklama
                                eklenecekUserPosts.postYuklenmeTarih=ds.getValue(Posts::class.java)!!.yuklenme_tarih

                                tumGonderiler.add(eklenecekUserPosts)

                            }
                        }

                        setupRecyclerView(1)

                    }

                })




            }


        })



    }

    //1 ise grid 2 ise list view şeklinde veriler gösterilir
    private fun setupRecyclerView(layoutCesidi: Int) {

        if(layoutCesidi==1){

            if (myRecyclerView!= null && myRecyclerView!!.getHandingVideoHolder() != null){
                myRecyclerView!!.getHandingVideoHolder().stopVideo();
                Log.e("HATA","Gridlayout aktif, videoları durdur")
            }


            imgGrid.setColorFilter(ContextCompat.getColor(this,R.color.mavi),PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this,R.color.siyah),PorterDuff.Mode.SRC_IN)
            myRecyclerView=profileRecyclerView
            myRecyclerView!!.setHasFixedSize(true)
            myRecyclerView!!.adapter=ProfilePostGridRecyclerAdapter(tumGonderiler,this)

            myRecyclerView!!.layoutManager=GridLayoutManager(this,3)

        }else if(layoutCesidi==2){

            if (myRecyclerView!= null && myRecyclerView?.getHandingVideoHolder() != null) {
                myRecyclerView!!.getHandingVideoHolder().playVideo();
                Log.e("HATA","Listview aktif, varsa bekleyen videoyu oynat")
            }

            imgGrid.setColorFilter(ContextCompat.getColor(this,R.color.siyah),PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this,R.color.mavi),PorterDuff.Mode.SRC_IN)
            myRecyclerView=profileRecyclerView
            myRecyclerView!!.layoutManager=CenterLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            myRecyclerView!!.adapter=ProfilePostListRecyclerAdapter(this,tumGonderiler)



        }

    }

    override fun onBackPressed() {
        tumlayout.visibility= View.VISIBLE
        profileContainer.visibility=View.INVISIBLE



        super.onBackPressed()
        overridePendingTransition(0,0)
    }

    private fun setupAuthListener() {



        mAuthListener=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser

                if(user == null){

                    Log.e("HATA","Kullanıcı oturum açmamış, ProfileActivitydesin")

                    var intent= Intent(this@ProfileActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }else {



                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("HATA","ProfileActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
        if (myRecyclerView!= null && myRecyclerView?.getHandingVideoHolder() != null) {
            myRecyclerView!!.getHandingVideoHolder().playVideo();
            Log.e("HATA","RESUME CALISIYO")
        }

    }

    override fun onPause() {
        super.onPause()
        if (myRecyclerView!= null && myRecyclerView!!.getHandingVideoHolder() != null){
            myRecyclerView!!.getHandingVideoHolder().stopVideo();
            Log.e("HATA","PAUSE CALISIYO")
        }
    }
}
