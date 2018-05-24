package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*

/**
 * Created by Emre on 24.05.2018.
 */
class ShareActivityGridViewAdapter(context: Context?, resource: Int, var klasordekiDosyalar: ArrayList<String>) : ArrayAdapter<String>(context, resource, klasordekiDosyalar) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var tek_sutun_resim=convertView
        var inflater=LayoutInflater.from(context)

        if(tek_sutun_resim== null){
            tek_sutun_resim=inflater.inflate(R.layout.tek_sutun_grid_resim, parent, false)
        }


        var imgView= tek_sutun_resim!!.imgTekSutunImage

        var imgURL=klasordekiDosyalar.get(position)

        UniversalImageLoader.setImage("file:/"+imgURL, imgView,null,"")

        return tek_sutun_resim

    }


}