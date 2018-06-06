package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Generic.CommentFragment
import com.emrealtunbilek.instakotlinapp.Home.HomeActivity
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.tek_post_recycler_item.view.*
import java.util.*
import kotlin.Comparator

/**
 * Created by Emre on 5.06.2018.
 */
class HomeFragmentRecyclerAdapter(var context: Context, var tumGonderiler: ArrayList<UserPosts>) : RecyclerView.Adapter<HomeFragmentRecyclerAdapter.MyViewHolder>() {

    init {
        Collections.sort(tumGonderiler, object : Comparator<UserPosts> {
            override fun compare(o1: UserPosts?, o2: UserPosts?): Int {
                if (o1!!.postYuklenmeTarih!! > o2!!.postYuklenmeTarih!!) {
                    return -1
                } else return 1
            }
        })

    }

    override fun getItemCount(): Int {
        return tumGonderiler.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var viewHolder = LayoutInflater.from(context).inflate(R.layout.tek_post_recycler_item, parent, false)

        return MyViewHolder(viewHolder, context)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(position, tumGonderiler.get(position))
    }


    class MyViewHolder(itemView: View?, myHomeActivity: Context) : RecyclerView.ViewHolder(itemView) {

        var tumLayout = itemView as ConstraintLayout
        var profileImage = tumLayout.imgUserProfile
        var userNameTitle = tumLayout.tvKullaniciAdiBaslik
        var gonderi = tumLayout.imgPostResim
        var userName = tumLayout.tvKullaniciAdi
        var gonderiAciklama = tumLayout.tvPostAciklama
        var gonderiKacZamanOnce = tumLayout.tvKacZamanOnce
        var yorumYap=tumLayout.imgYorum
        var myHomeActivity=myHomeActivity


        fun setData(position: Int, oankiGonderi: UserPosts) {

            userNameTitle.setText(oankiGonderi.userName)
            UniversalImageLoader.setImage(oankiGonderi.postURL!!, gonderi, null, "")
            userName.setText(oankiGonderi.userName)
            gonderiAciklama.setText(oankiGonderi.postAciklama)
            UniversalImageLoader.setImage(oankiGonderi.userPhotoURL!!, profileImage, null, "")
            gonderiKacZamanOnce.setText(TimeAgo.getTimeAgo(oankiGonderi.postYuklenmeTarih!!))



            yorumYap.setOnClickListener {

                (myHomeActivity as HomeActivity).homeViewPager.visibility=View.INVISIBLE
                (myHomeActivity as HomeActivity).homeFragmentContainer.visibility=View.VISIBLE


                var transaction=(myHomeActivity as HomeActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.homeFragmentContainer,CommentFragment())
                transaction.addToBackStack("commentFragmentEklendi")
                transaction.commit()
            }

        }


    }
}