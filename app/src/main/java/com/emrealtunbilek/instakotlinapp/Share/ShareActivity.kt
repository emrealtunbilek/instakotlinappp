package com.emrealtunbilek.instakotlinapp.Share

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.SharePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {

    private val ACTIVITY_NO=2
    private val TAG="ShareActivity"




    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupAuthListener()

        setupShareViewPager()
    }

    private fun setupShareViewPager() {

        var tabAdlari=ArrayList<String>()
        tabAdlari.add("GALERI")
        tabAdlari.add("FOTOĞRAF")
        tabAdlari.add("VIDEO")

        var sharePagerAdapter=SharePagerAdapter(supportFragmentManager,tabAdlari)
        sharePagerAdapter.addFragment(ShareGalleryFragment())
        sharePagerAdapter.addFragment(ShareCameraFragment())
        sharePagerAdapter.addFragment(ShareVideoFragment())


        shareViewPager.adapter=sharePagerAdapter
        sharetablayout.setupWithViewPager(shareViewPager)


    }

    override fun onBackPressed() {

        anaLayout.visibility= View.VISIBLE
        fragmentContainerLayout.visibility=View.GONE
        super.onBackPressed()
    }

    private fun setupAuthListener() {



        mAuthListener=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser

                if(user == null){

                    Log.e("HATA","Kullanıcı oturum açmamış, ProfileActivitydesin")

                    var intent= Intent(this@ShareActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }else {



                }
            }

        }
    }


}
