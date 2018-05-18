package com.emrealtunbilek.instakotlinapp.Profile


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog


import com.emrealtunbilek.instakotlinapp.R
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var alert = AlertDialog.Builder(this!!.activity!!)
                .setTitle("Instagram'dan Çıkış Yap")
                .setMessage("Emin misiniz ?")
                .setPositiveButton("Çıkış Yap", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                        FirebaseAuth.getInstance().signOut()
                        activity!!.finish()
                    }

                })
                .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dismiss()
                    }
                }).create()

        return alert
    }




}
