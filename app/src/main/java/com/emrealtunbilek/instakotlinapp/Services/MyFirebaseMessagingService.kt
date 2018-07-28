package com.emrealtunbilek.instakotlinapp.Services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.emrealtunbilek.instakotlinapp.Generic.UserProfileActivity
import com.emrealtunbilek.instakotlinapp.Home.ChatActivity
import com.emrealtunbilek.instakotlinapp.Home.HomeActivity
import com.emrealtunbilek.instakotlinapp.Home.MessagesFragment
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.Bildirimler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Emre on 14.07.2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {

        if(p0!!.data != null){

            if(p0!!.data!!.get("bildirimTuru")!!.toString().equals("yeni_mesaj")){

                var mesajGonderenUserName= p0!!.data.get("kimYolladi")
                var sonMesaj = p0!!.data.get("neYolladi")
                var mesajGonderenUserID=p0!!.data.get("secilenUserID")

                if(ChatActivity.activityAcikMi==false && MessagesFragment.fragmentAcikMi==false)
                yeniMesajBildiriminiGoster("Yeni Mesaj",mesajGonderenUserName +" : "+sonMesaj,mesajGonderenUserID)


            }else if(p0!!.data!!.get("bildirimTuru")!!.toString().equals("yeni_takip_istek")){

                var kimYolladi=p0!!.data.get("kimYolladi")
                var takipEtmekIsteyenUserID=p0!!.data.get("secilenUserID")

                Log.e("FCM","BİLDİRİM GELDİ : "+p0!!.data)

                yeniTakipBildiriminiGoster("Yeni Takip İsteği",kimYolladi +" seni takip etmek istiyor",takipEtmekIsteyenUserID)


            }else if(p0!!.data.get("bildirimTuru")!!.toString().equals("takip_istek_kabul_edildi")){
                var istegiKabulEdenUserName=p0!!.data.get("kimYolladi")
                var istegiKabulEdenUserID=p0!!.data.get("secilenUserID")

                Log.e("FCM","BİLDİRİM GELDİ : "+p0!!.data)

                takipIstekKabulEdildiBildiriminiGoster("Takip İsteği Onaylandı",istegiKabulEdenUserName +" kullanıcısı takip isteğini kabul etti",istegiKabulEdenUserID)
                Log.e("FCM","BİLDİRİM kaydedilecek : "+p0!!.data)
                Bildirimler.bildirimKaydet(istegiKabulEdenUserID!!,Bildirimler.TAKIP_ISTEGI_ONAYLANDI)
            }


        }





    }

    private fun yeniMesajBildiriminiGoster(bildirimBaslik: String?, bildirimBody: String?, gidilecekUserID: String?) {

        var pendingIntent=Intent(this,HomeActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("secilenUserID",gidilecekUserID)

        var bildirimPendingIntent=PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder=NotificationCompat.Builder(this,"Yeni Mesaj")
                .setSmallIcon(R.drawable.ic_new_notify)
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

    private fun yeniTakipBildiriminiGoster(bildirimBaslik: String?, bildirimBody: String?, takipEtmekIsteyenUserID: String?) {

        var pendingIntent=Intent(this,HomeActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("bildirim","yeni_takip_istegi")

        var bildirimPendingIntent=PendingIntent.getActivity(this,15,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder=NotificationCompat.Builder(this,"Yeni Takip isteği")
                .setSmallIcon(R.drawable.ic_new_notify)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_yeni_takip_istek))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(bildirimBaslik)
                .setContentText(bildirimBody)
                .setAutoCancel(true)
                .setContentIntent(bildirimPendingIntent)
                .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bildirimIDOlustur(takipEtmekIsteyenUserID!!),builder)

    }

    private fun takipIstekKabulEdildiBildiriminiGoster(bildirimBaslik: String?, bildirimBody: String?, takipIsteginiKabulEdenUserID: String?) {

        var pendingIntent=Intent(this,HomeActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("gidilecekUserID",takipIsteginiKabulEdenUserID)

        var bildirimPendingIntent=PendingIntent.getActivity(this,15,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder=NotificationCompat.Builder(this,"Takip Başladı")
                .setSmallIcon(R.drawable.ic_new_notify)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_yeni_takip_istek))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(bildirimBaslik)
                .setContentText(bildirimBody)
                .setAutoCancel(true)
                .setContentIntent(bildirimPendingIntent)
                .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bildirimIDOlustur(takipIsteginiKabulEdenUserID!!),builder)

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