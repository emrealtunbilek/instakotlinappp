package com.emrealtunbilek.instakotlinapp.Models

/**
 * Created by Emre on 6.06.2018.
 */
class Comments {

    var user_id:String?=null
    var yorum:String?=null
    var yorum_begeni:String?=null
    var yorum_tarih:Long?=null

    constructor(user_id: String?, yorum: String?, yorum_begeni: String?, yorum_tarih: Long?) {
        this.user_id = user_id
        this.yorum = yorum
        this.yorum_begeni = yorum_begeni
        this.yorum_tarih = yorum_tarih
    }

    constructor(){}

    override fun toString(): String {
        return "Comments(user_id=$user_id, yorum=$yorum, yorum_begeni=$yorum_begeni, yorum_tarih=$yorum_tarih)"
    }


}