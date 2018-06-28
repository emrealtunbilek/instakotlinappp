package com.emrealtunbilek.instakotlinapp.Models

/**
 * Created by Emre on 28.06.2018.
 */
class Mesaj {

     var mesaj:String? = null
     var goruldu:Boolean? =null
     var time:Long? = null
     var type:String? = null
     var user_id:String? = null

    constructor(){}
    constructor(mesaj: String?, goruldu: Boolean?, time: Long?, type: String?, user_id: String?) {
        this.mesaj = mesaj
        this.goruldu = goruldu
        this.time = time
        this.type = type
        this.user_id = user_id
    }

    override fun toString(): String {
        return "Mesaj(mesaj=$mesaj, goruldu=$goruldu, time=$time, type=$type, user_id=$user_id)"
    }


}