package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.Mesaj
import com.emrealtunbilek.instakotlinapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.tek_satir_mesaj_gonderen.view.*

/**
 * Created by Emre on 28.06.2018.
 */
class MesajRecyclerViewAdapter(var tumMesajlar:ArrayList<Mesaj>, var myContext:Context) : RecyclerView.Adapter<MesajRecyclerViewAdapter.MyMesajViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyMesajViewHolder {

        var myView:View?=null

        if(viewType==1){
            myView=LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_gonderen,parent,false)


        }else if(viewType==2) {
            myView=LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_alan,parent,false)
        }

        return MyMesajViewHolder(myView)

    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    override fun onBindViewHolder(holder:MyMesajViewHolder, position: Int) {
        holder.setData(tumMesajlar.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        if(tumMesajlar.get(position).user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            return 1
        }else return 2
    }



    class MyMesajViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumlayout=itemView as ConstraintLayout
        var mesajText=tumlayout.tvMesaj



        fun setData(oankiMesaj: Mesaj) {
            mesajText.text=oankiMesaj.mesaj
        }

    }


}