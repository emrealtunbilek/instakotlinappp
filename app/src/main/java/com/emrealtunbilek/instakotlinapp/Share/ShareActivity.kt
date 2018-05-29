package com.emrealtunbilek.instakotlinapp.Share

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.Manifest
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.SharePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {

    private val ACTIVITY_NO=2
    private val TAG="ShareActivity"




    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupAuthListener()

        storageVeKameraIzniIste()


    }

    private fun storageVeKameraIzniIste() {

        Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                 android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                 android.Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        if(report!!.areAllPermissionsGranted()){
                            Log.e("HATA","Tüm izinler verilmiş")
                            setupShareViewPager()
                        }
                        if(report!!.isAnyPermissionPermanentlyDenied){
                            Log.e("HATA","izinlerden birine bidaha sorma denmiş")
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {

                        Log.e("HATA","izinlerden biri reddedilmiş, kullanıcıyı ikna et")
                        token!!.continuePermissionRequest()
                       // token!!.cancelPermissionRequest()

                    }

                })
                .withErrorListener(object : PermissionRequestErrorListener{
                    override fun onError(error: DexterError?) {
                        Log.e("HATA", error!!.toString())
                    }

                }).check()


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
