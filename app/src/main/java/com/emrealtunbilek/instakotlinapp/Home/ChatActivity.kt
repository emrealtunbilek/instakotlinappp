package com.emrealtunbilek.instakotlinapp.Home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.emrealtunbilek.instakotlinapp.R

class ChatActivity : AppCompatActivity() {

    lateinit var sohbetEdilecekUserId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sohbetEdilecekUserId=intent.getStringExtra("secilenUserID")
        Toast.makeText(this,"Secilen id :"+sohbetEdilecekUserId,Toast.LENGTH_SHORT).show()
    }
}
