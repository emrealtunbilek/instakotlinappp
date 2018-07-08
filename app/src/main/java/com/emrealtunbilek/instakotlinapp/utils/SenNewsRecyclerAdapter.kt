package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.BildirimModel
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import java.util.*

/**
 * Created by Emre on 8.07.2018.
 */
class SenNewsRecyclerAdapter(var context: Context, var tumBildirimler: ArrayList<BildirimModel>):RecyclerView.Adapter<> {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): {

    }

    override fun getItemCount(): Int {
      return tumBildirimler.size
    }

    override fun onBindViewHolder(holder:, position: Int) {

    }

    init {
        Collections.sort(tumBildirimler, object : Comparator<BildirimModel> {
            override fun compare(o1: BildirimModel?, o2: BildirimModel?): Int {
                if (o1!!.time!! > o2!!.time!!) {
                    return -1
                } else return 1
            }
        })

    }
}