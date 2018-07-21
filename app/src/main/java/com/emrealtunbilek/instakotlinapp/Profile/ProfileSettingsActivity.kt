package com.emrealtunbilek.instakotlinapp.Profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.emrealtunbilek.instakotlinapp.Login.LoginActivity
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.android.synthetic.main.fragment_share_next.*


class ProfileSettingsActivity : AppCompatActivity() {

    private val ACTIVITY_NO=4
    private val TAG="ProfileActivity"
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)


        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef= FirebaseDatabase.getInstance().reference


        setupToolbar()
        fragmentNavigations()

        mRef.child("users").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.hasChild("gizli_profil")){

                    switchGizli.isChecked=p0!!.child("gizli_profil").getValue().toString().toBoolean()


                }
            }


        })

        containerGizli.setOnClickListener {

            if(switchGizli.isChecked){

                switchGizli.isChecked=false
                mRef.child("users").child(mUser.uid).child("gizli_profil").setValue(false)

            }else {

                mRef.child("users").child(mUser.uid).child("gizli_profil").setValue(true)
                switchGizli.isChecked=true

            }


        }

    }

    private fun fragmentNavigations() {

        tvProfilDuzenleHesapAyarlari.setOnClickListener {
            profileSettingsRoot.visibility=View.GONE
            profileSettingsContainer.visibility=View.VISIBLE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer,ProfileEditFragment())
            transaction.addToBackStack("editProfileFragmentEklendi")
            transaction.commit()
        }

        tvBegendiginGonderiler.setOnClickListener {
            profileSettingsRoot.visibility=View.GONE
            profileSettingsContainer.visibility=View.VISIBLE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer,BegendigimGonderilerFragment())
            transaction.addToBackStack("begendigimGonderilerFragmentEklendi")
            transaction.commit()
        }


        tvCikisYap.setOnClickListener {

            var dialog=SignOutFragment()
            dialog.show(supportFragmentManager,"cikisYapDialogGoster")


        }


    }


    private fun setupToolbar() {
        imgBack.setOnClickListener {
           onBackPressed()
        }
    }

    override fun onBackPressed() {
        profileSettingsRoot.visibility=View.VISIBLE
        profileSettingsContainer.visibility=View.GONE
        super.onBackPressed()
        overridePendingTransition(0,0)
    }


    fun setupNavigationView(){

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView,ACTIVITY_NO)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun setupAuthListener() {



        mAuthListener=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser

                if(user == null){

                    Log.e("HATA","Kullanıcı oturum açmamış, ProfileActivitydesin")

                    var intent= Intent(this@ProfileSettingsActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }else {



                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        setupNavigationView()
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
}
