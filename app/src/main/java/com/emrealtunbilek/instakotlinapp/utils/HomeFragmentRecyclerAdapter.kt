package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_post_recycler_item.view.*

/**
 * Created by Emre on 5.06.2018.
 */
class HomeFragmentRecyclerAdapter(var context:Context, var tumGonderiler:ArrayList<UserPosts>):RecyclerView.Adapter<HomeFragmentRecyclerAdapter.MyViewHolder>() {


    override fun getItemCount(): Int {
        return tumGonderiler.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var viewHolder=LayoutInflater.from(context).inflate(R.layout.tek_post_recycler_item,parent,false)

        return MyViewHolder(viewHolder)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(position, tumGonderiler.get(position))
    }


    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumLayout=itemView as ConstraintLayout
        var profileImage=tumLayout.imgUserProfile
        var userNameTitle=tumLayout.tvKullaniciAdiBaslik
        var gonderi=tumLayout.imgPostResim
        var userName=tumLayout.tvKullaniciAdi
        var gonderiAciklama=tumLayout.tvPostAciklama


        fun setData(position: Int, oankiGonderi:UserPosts) {

           userNameTitle.setText(oankiGonderi.userName)
           UniversalImageLoader.setImage(oankiGonderi.postURL!!,gonderi,null,"")
           userName.setText(oankiGonderi.userName)
           gonderiAciklama.setText(oankiGonderi.postAciklama)
            UniversalImageLoader.setImage(oankiGonderi.userPhotoURL!!,profileImage,null,"")

        }


    }
}