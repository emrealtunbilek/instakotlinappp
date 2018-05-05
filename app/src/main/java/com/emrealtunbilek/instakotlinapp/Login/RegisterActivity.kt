package com.emrealtunbilek.instakotlinapp.Login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {


    lateinit var manager:FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        manager=supportFragmentManager
        manager.addOnBackStackChangedListener(this)
        init()

    }

    private fun init() {

        tvEposta.setOnClickListener {
            viewTelefon.visibility = View.INVISIBLE
            viewEposta.visibility = View.VISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etGirisYontemi.setHint("E-Posta")

            btnIleri.isEnabled=false
            btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
            btnIleri.setBackgroundResource(R.drawable.register_button)

        }

        tvTelefon.setOnClickListener {
            viewTelefon.visibility = View.VISIBLE
            viewEposta.visibility = View.INVISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType = InputType.TYPE_CLASS_PHONE
            etGirisYontemi.setHint("Telefon")

            btnIleri.isEnabled=false
            btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
            btnIleri.setBackgroundResource(R.drawable.register_button)

        }

        etGirisYontemi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (start+before+count >= 10) {

                    btnIleri.isEnabled=true
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.beyaz))
                    btnIleri.setBackgroundResource(R.drawable.register_button_aktif)
                }else {

                    btnIleri.isEnabled=false
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
                    btnIleri.setBackgroundResource(R.drawable.register_button)
                }
            }

        })

        btnIleri.setOnClickListener {

            if(etGirisYontemi.hint.toString().equals("Telefon")){

                loginRoot.visibility=View.GONE
                loginContainer.visibility=View.VISIBLE
                var transaction=supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginContainer,TelefonKoduGirFragment())
                transaction.addToBackStack("telefonKoduGirFragmentEklendi")
                transaction.commit()
                EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(etGirisYontemi.text.toString(),null,null,null, false))


            }
            else {
                loginRoot.visibility=View.GONE
                loginContainer.visibility=View.VISIBLE
                var transaction=supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginContainer, KayitFragment())
                transaction.addToBackStack("emailileGirisFragmentEklendi")
                transaction.commit()
                EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(null,etGirisYontemi.text.toString(), null, null, true))
            }

        }


    }

    override fun onBackStackChanged() {
        val elemanSayisi = manager.backStackEntryCount

        if(elemanSayisi==0){
            loginRoot.visibility= View.VISIBLE
        }
    }


}
