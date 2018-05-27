package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
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
        lateinit var tvSure :TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        tekSutunResim=convertView


        if(tekSutunResim== null){
            tekSutunResim=inflater.inflate(R.layout.tek_sutun_grid_resim, parent, false)
            viewHolder=ViewHolder()

            viewHolder.imageView=tekSutunResim!!.imgTekSutunImage
            viewHolder.progressBar=tekSutunResim!!.progressBar
            viewHolder!!.tvSure=tekSutunResim!!.tvSure

            tekSutunResim!!.setTag(viewHolder)

        }else {

            viewHolder= tekSutunResim!!.getTag() as ViewHolder

        }

        var dosyaYolu=klasordekiDosyalar.get(position)
        var dosyaTuru=dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if(dosyaTuru.equals(".mp4")){

            viewHolder.tvSure.visibility=View.VISIBLE
            var retriver=MediaMetadataRetriever()
            retriver.setDataSource(context, Uri.parse("file://"+dosyaYolu))

            var videoSuresi=retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var videoSuresiLong=videoSuresi.toLong()


            viewHolder.tvSure.setText(convertDuration(videoSuresiLong))
            UniversalImageLoader.setImage(klasordekiDosyalar.get(position), viewHolder.imageView, viewHolder.progressBar,"file:/")

        }else {

            viewHolder.tvSure.visibility=View.GONE
            UniversalImageLoader.setImage(klasordekiDosyalar.get(position), viewHolder.imageView, viewHolder.progressBar,"file:/")


        }




        return tekSutunResim!!

    }

    fun convertDuration(duration: Long): String {
        var out: String? = null
        var hours: Long = 0
        try {
            hours = duration / 3600000
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return out!!
        }

        val remaining_minutes = (duration - hours * 3600000) / 60000
        var minutes = remaining_minutes.toString()
        if (minutes.equals("0")) {
            minutes = "00"
        }
        val remaining_seconds = duration - hours * 3600000 - remaining_minutes * 60000
        var seconds = remaining_seconds.toString()
        if (seconds.length < 2) {
            seconds = "00"
        } else {
            seconds = seconds.substring(0, 2)
        }

        if (hours > 0) {
            out = hours.toString() + ":" + minutes + ":" + seconds
        } else {
            out = minutes + ":" + seconds
        }

        return out

    }


}