package com.emrealtunbilek.instakotlinapp.utils

import com.emrealtunbilek.instakotlinapp.Models.Users

/**
 * Created by Emre on 1.05.2018.
 */

class EventbusDataEvents {

    internal class KayitBilgileriniGonder(var telNo:String?, var email:String?, var verificationID:String?, var code:String?, var emailkayit:Boolean )

    internal class KullaniciBilgileriniGonder(var kullanici:Users?)

    internal class PaylasilacakResmiGonder(var dosyaYolu:String?, var dosyaTuruResimMi:Boolean?)

}
