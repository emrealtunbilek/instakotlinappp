package com.emrealtunbilek.instakotlinapp.Models

/**
 * Created by Emre on 8.07.2018.
 */
class BildirimModel {

    var takip_ettigimin_user_id:String?=null
    var bildirim_tur:Int?=null
    var time:Long?=null
    var user_id:String?=null
    var gonderi_id:String?=null

    constructor(){}



    constructor(bildirim_tur: Int?, time: Long?, user_id: String?, gonderi_id: String?) {
        this.bildirim_tur = bildirim_tur
        this.time = time
        this.user_id = user_id
        this.gonderi_id = gonderi_id
    }

    constructor(bildirim_tur: Int?, time: Long?, user_id: String?) {
        this.bildirim_tur = bildirim_tur
        this.time = time
        this.user_id = user_id
    }

    constructor(takip_ettigimin_user_id: String?, bildirim_tur: Int?, time: Long?, user_id: String?, gonderi_id: String?) {
        this.takip_ettigimin_user_id = takip_ettigimin_user_id
        this.bildirim_tur = bildirim_tur
        this.time = time
        this.user_id = user_id
        this.gonderi_id = gonderi_id
    }

    constructor(takip_ettigimin_user_id: String?, bildirim_tur: Int?, time: Long?, user_id: String?) {
        this.takip_ettigimin_user_id = takip_ettigimin_user_id
        this.bildirim_tur = bildirim_tur
        this.time = time
        this.user_id = user_id
    }


    override fun toString(): String {
        return "BildirimModel(bildirim_tur=$bildirim_tur, time=$time, user_id=$user_id)"
    }


}