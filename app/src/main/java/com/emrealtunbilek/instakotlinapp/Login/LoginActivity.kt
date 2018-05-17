package com.emrealtunbilek.instakotlinapp.Login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        init()

    }

    fun init() {
        etEmailTelorUsername.addTextChangedListener(watcher)
        etSifre.addTextChangedListener(watcher)

        btnGirisYap.setOnClickListener {
            //kullanıcı veritabanında aranır, bulunursa giriş yapma denemesi yapılır
            oturumAcacakKullaniciyiDenetle(etEmailTelorUsername.text.toString(), etSifre.text.toString())
        }
    }

    private fun oturumAcacakKullaniciyiDenetle(emailPhoneNumberUserName: String, sifre: String) {

        mRef.child("users").orderByChild("email").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                for (ds in p0!!.children) {

                    var okunanKullanici = ds.getValue(Users::class.java)

                    if (okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)) {

                        oturumAc(okunanKullanici, sifre, false)
                        break

                    } else if (okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)) {
                        oturumAc(okunanKullanici, sifre, false)
                        break
                    } else if (okunanKullanici!!.phone_number!!.toString().equals(emailPhoneNumberUserName)) {

                        oturumAc(okunanKullanici, sifre, true)
                        break
                    }


                }
            }


        })


    }

    private fun oturumAc(okunanKullanici: Users, sifre: String, telefonIleGiris: Boolean) {

        var girisYapacakEmail = ""

        if (telefonIleGiris == true) {
            girisYapacakEmail = okunanKullanici.email_phone_number.toString()
        } else {
            girisYapacakEmail = okunanKullanici.email.toString()
        }

        mAuth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0!!.isSuccessful) {
                            Toast.makeText(this@LoginActivity, " Oturum açıldı :" + mAuth.currentUser!!.uid, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, " Kullanıcı Adı/Sifre Hatalı :", Toast.LENGTH_SHORT).show()
                        }
                    }

                })


    }


    var watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (etEmailTelorUsername.text.toString().length >= 6 && etSifre.text.toString().length >= 6) {

                btnGirisYap.isEnabled = true
                btnGirisYap.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.beyaz))
                btnGirisYap.setBackgroundResource(R.drawable.register_button_aktif)

            } else {
                btnGirisYap.isEnabled = false
                btnGirisYap.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.sonukmavi))
                btnGirisYap.setBackgroundResource(R.drawable.register_button)
            }

        }

    }
}
