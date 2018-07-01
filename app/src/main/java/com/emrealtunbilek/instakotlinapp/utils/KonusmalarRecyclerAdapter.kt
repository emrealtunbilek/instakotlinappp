package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.Konusmalar
import com.emrealtunbilek.instakotlinapp.Models.Users
import com.emrealtunbilek.instakotlinapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_satir_konusma_item.view.*

/**
 * Created by Emre on 2.07.2018.
 */
class KonusmalarRecyclerAdapter(var tumKonusmalar:ArrayList<Konusmalar>, var myContext:Context): RecyclerView.Adapter<KonusmalarRecyclerAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var myView=LayoutInflater.from(myContext).inflate(R.layout.tek_satir_konusma_item, parent,false)

        return MyViewHolder(myView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setData(tumKonusmalar.get(position))
    }

    override fun getItemCount(): Int {
        return tumKonusmalar.size
    }




    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumLayout=itemView as ConstraintLayout

        var enSonAtilanMesaj=tumLayout.tvSonMesaj
        var mesajZaman = tumLayout.tvMesajZaman
        var sohbetEdilenUserName=tumLayout.tvUserName
        var sohbetEdilenUserPic=tumLayout.imgUserProfilePicture



        fun setData(oankiKonusma: Konusmalar) {

            enSonAtilanMesaj.text=oankiKonusma.son_mesaj.toString()
            mesajZaman.text=TimeAgo.getTimeAgoForComments(oankiKonusma.time!!.toLong())

            sohbetEdilenKullaniciBilgileriniGetir(oankiKonusma.user_id.toString())

        }

        private fun sohbetEdilenKullaniciBilgileriniGetir(userID: String) {

            FirebaseDatabase.getInstance().getReference().child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                   if(p0!!.getValue() != null ){


                       sohbetEdilenUserName.text=p0.child("user_name").getValue().toString()
                       var userProfilePictureURL=p0.child("user_detail").child("profile_picture").getValue().toString()
                       UniversalImageLoader.setImage(userProfilePictureURL,sohbetEdilenUserPic,null,"")



                   }
                }


            })

        }


    }


}