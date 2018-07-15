package com.emrealtunbilek.instakotlinapp.Services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.emrealtunbilek.instakotlinapp.Home.HomeActivity
import com.emrealtunbilek.instakotlinapp.R
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

        var bildirimData=p0!!.data.get("secilenUserID")

        Log.e("FCM","BAŞLIK : $bildirimBaslik gövde: $bildirimBody")

        if(bildirimBaslik.equals("Yeni Mesaj")){

            yeniMesajBildiriminiGoster(bildirimBaslik, bildirimBody, bildirimData)

        }else {
            bildirimGoster(bildirimBaslik, bildirimBody)
        }



    }

    private fun yeniMesajBildiriminiGoster(bildirimBaslik: String?, bildirimBody: String?, gidilecekUserID: String?) {

        var pendingIntent=Intent(this,HomeActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("secilenUserID",gidilecekUserID)

        var bildirimPendingIntent=PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder=NotificationCompat.Builder(this,"Yeni Mesaj")
                .setSmallIcon(R.drawable.ic_yeni_mesaj_notif)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_yeni_mesaj_notif))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(bildirimBaslik)
                .setContentText(bildirimBody)
                .setColor(getColor(R.color.mavi)).setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(bildirimPendingIntent)
                .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bildirimIDOlustur(gidilecekUserID!!),builder)


    }

    private fun bildirimIDOlustur(gidilecekUserID: String): Int{
        var id= 0

        for(i in 0..5){
            id= id + gidilecekUserID[i].toInt()
        }

        return id
    }

    private fun bildirimGoster(bildirimBaslik: String?, bildirimBody: String?) {

        var pendingIntent=Intent(this,HomeActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("bildirim","yeni_takip_istegi")

        var bildirimPendingIntent=PendingIntent.getActivity(this,15,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder=NotificationCompat.Builder(this,"Yeni Takip isteği")
                .setSmallIcon(R.drawable.ic_yeni_takip_istek)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_yeni_takip_istek))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(bildirimBaslik)
                .setContentText(bildirimBody)
                .setAutoCancel(true)
                .setContentIntent(bildirimPendingIntent)
                .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(),builder)

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