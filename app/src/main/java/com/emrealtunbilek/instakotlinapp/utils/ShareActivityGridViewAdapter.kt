package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*

/**
 * Created by Emre on 24.05.2018.
 */
class ShareActivityGridViewAdapter(context: Context?, resource: Int, var klasordekiDosyalar: ArrayList<String>) : ArrayAdapter<String>(context, resource, klasordekiDosyalar) {


    var inflater: LayoutInflater
    var tekSutunResim:View? = null
    lateinit var viewHolder:ViewHolder

    init {
        inflater=LayoutInflater.from(context)
    }

    inner class ViewHolder(){
        lateinit var imageView:GridImageView
        lateinit var progressBar: ProgressBar
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        tekSutunResim=convertView


        if(tekSutunResim== null){
            tekSutunResim=inflater.inflate(R.layout.tek_sutun_grid_resim, parent, false)
            viewHolder=ViewHolder()

            viewHolder.imageView=tekSutunResim!!.imgTekSutunImage
            viewHolder.progressBar=tekSutunResim!!.progressBar

            tekSutunResim!!.setTag(viewHolder)

        }else {

            viewHolder= tekSutunResim!!.getTag() as ViewHolder

        }


        UniversalImageLoader.setImage(klasordekiDosyalar.get(position), viewHolder.imageView, viewHolder.progressBar,"file:/")

        return tekSutunResim!!

    }


}