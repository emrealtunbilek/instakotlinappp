package com.emrealtunbilek.instakotlinapp.Models

/**
 * Created by Emre on 28.06.2018.
 */
class Mesaj {

    private var mesaj:String? = null
    private var goruldu:Boolean? =null
    private var time:Long? = null
    private var type:String? = null
    private var user_id:String? = null

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