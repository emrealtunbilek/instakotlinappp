package com.emrealtunbilek.instakotlinapp.Models

/**
 * Created by Emre on 16.05.2018.
 */

class Users {

    var email: String? = null
    var password: String? = null
    var user_name: String? = null
    var adi_soyadi: String? = null
    var phone_number: String? = null
    var email_phone_number: String? = null
    var user_id: String? = null

    constructor() {}

    constructor(email: String, password: String, user_name: String, adi_soyadi: String, user_id:String) {
        this.email = email
        this.password = password
        this.user_name = user_name
        this.adi_soyadi = adi_soyadi
        this.user_id = user_id
    }

    constructor(password: String, user_name: String, adi_soyadi: String, phone_number: String, email_phone_number: String,user_id:String) {
        this.password = password
        this.user_name = user_name
        this.adi_soyadi = adi_soyadi
        this.phone_number = phone_number
        this.email_phone_number = email_phone_number
        this.user_id = null
    }
}
