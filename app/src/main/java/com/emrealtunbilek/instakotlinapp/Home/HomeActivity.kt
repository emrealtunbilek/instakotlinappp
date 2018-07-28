package com.emrealtunbilek.instakotlinapp.Home

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.emrealtunbilek.instakotlinapp.Generic.UserProfileActivity
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.News.NewsActivity
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.emrealtunbilek.instakotlinapp.utils.HomePagerAdapter
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus

class HomeActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 0
    private val TAG = "HomeActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()

        initImageLoader()
        setupHomeViewPager()
        if(intent.extras != null){

            if(intent.extras.get("secilenUserID") != null){
                var secilenUserID=intent.extras.getString("secilenUserID")
                var intent=Intent(this,ChatActivity::class.java)
                intent.putExtra("secilenUserID",secilenUserID)
                startActivity(intent)
            }else if(intent.extras.get("gidilecekUserID") != null){
                var secilenUserID=intent.extras.getString("gidilecekUserID")
                var intent=Intent(this,UserProfileActivity::class.java)
                intent.putExtra("secilenUserID",secilenUserID)
                startActivity(intent)
            }

            else {
                var intent=Intent(this,NewsActivity::class.java)
                intent.putExtra("bildirim","yeni_takip_istegi")
                startActivity(intent)
            }
        }


    }

    override fun onResume() {
        super.onResume()
    }


    private fun setupHomeViewPager() {
        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment()) //id = 0
        homePagerAdapter.addFragment(HomeFragment()) //id =1
        homePagerAdapter.addFragment(MessagesFragment())//id =2

        //activity mainde bulunan viewpagera olusturudugumuz adaptörü atadık
        homeViewPager.adapter = homePagerAdapter

        //viewpagerın homefragment ile baslamasını sagladık
        homeViewPager.setCurrentItem(1)
        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 0)
        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 2)

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)


                    storageVeKameraIzniIste()

                        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 1)
                        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 2)
                        homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager, 0)

                }

                if (position == 1) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 2)
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager, 1)
                }

                if (position == 2) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 1)
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager, 2)
                }
            }


        })

    }

    private fun storageVeKameraIzniIste() {



        Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        if (report!!.areAllPermissionsGranted()) {
                            //Log.e("HATA", "Tüm izinler verilmiş")

                            EventBus.getDefault().postSticky(EventbusDataEvents.KameraIzinBilgisiGonder(true))
                        }
                        if (report!!.isAnyPermissionPermanentlyDenied) {
                            //Log.e("HATA", "izinlerden birine bidaha sorma denmiş")

                            var builder = AlertDialog.Builder(this@HomeActivity)
                            builder.setTitle("İzin Gerekli")
                            builder.setMessage("Ayarlar kısmından uygulamaya izin vermeniz gerekiyor. Onaylar mısınız ?")
                            builder.setPositiveButton("AYARLARA GİT", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.cancel()
                                    var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    var uri = Uri.fromParts("package", packageName, null)
                                    intent.setData(uri)
                                    startActivity(intent)
                                    finish()
                                }

                            })
                            builder.setNegativeButton("IPTAL", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog!!.cancel()

                                    homeViewPager.setCurrentItem(1)
                                    finish()
                                }

                            })
                            builder.show()

                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {

                        //Log.e("HATA", "izinlerden biri reddedilmiş, kullanıcıyı ikna et")

                        var builder = AlertDialog.Builder(this@HomeActivity)
                        builder.setTitle("İzin Gerekli")
                        builder.setMessage("Uygulamaya izin vermeniz gerekiyor. Onaylar mısınız ?")
                        builder.setPositiveButton("ONAY VER", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()

                                token!!.continuePermissionRequest()
                                homeViewPager.setCurrentItem(1)
                            }

                        })
                        builder.setNegativeButton("IPTAL", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()

                                token!!.cancelPermissionRequest()
                                homeViewPager.setCurrentItem(1)

                            }

                        })
                        builder.show()


                    }

                })
                .withErrorListener(object : PermissionRequestErrorListener {
                    override fun onError(error: DexterError?) {
                        //Log.e("HATA", error!!.toString())
                    }

                }).check()


    }

    override fun onBackPressed() {

        if(homeViewPager.currentItem == 1){

            homeViewPager.visibility=View.VISIBLE
            homeFragmentContainer.visibility=View.GONE

            super.onBackPressed()
            overridePendingTransition(0,0)

        }else {
            homeViewPager.visibility=View.VISIBLE
            homeFragmentContainer.visibility=View.GONE
            homeViewPager.setCurrentItem(1)
        }

    }

    private fun initImageLoader() {

        var universalImageLoader = UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)

    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    //Log.e("HATA", "Kullanıcı oturum açmamış, HomeActivitydesn")
                    var intent = Intent(this@HomeActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
        //Log.e("HATA", "HomeActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

}
