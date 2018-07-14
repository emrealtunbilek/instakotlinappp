package com.emrealtunbilek.instakotlinapp.Services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Emre on 14.07.2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {

        var bildirimBaslik=p0!!.notification!!.title
        var bildirimBody = p0!!.notification!!.body
        var bildirimData=p0!!.data

        Log.e("FCM","BAŞLIK : $bildirimBaslik gövde: $bildirimBody data:$bildirimData")

    }

    override fun onNewToken(token: String?) {
        var yeniToken=token!!
        yeniTokenVeritabaninaKaydet(yeniToken)
    }

    private fun yeniTokenVeritabaninaKaydet(yeniToken: String) {
        if(FirebaseAuth.getInstance().currentUser != null ){
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("fcm_token").setValue(yeniToken)
        }
    }
}