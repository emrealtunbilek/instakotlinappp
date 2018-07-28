package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.UserPosts
import com.emrealtunbilek.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim_profil.view.*
import android.os.Build
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import com.emrealtunbilek.instakotlinapp.Generic.TekGonderiFragment
import com.emrealtunbilek.instakotlinapp.Generic.TekGonderiSearchFragment
import com.emrealtunbilek.instakotlinapp.Profile.BegendigimGonderilerFragment
import com.emrealtunbilek.instakotlinapp.Profile.ProfileActivity
import com.emrealtunbilek.instakotlinapp.Profile.ProfileSettingsActivity
import com.emrealtunbilek.instakotlinapp.Search.SearchActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.android.synthetic.main.activity_search.*
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * Created by Emre on 3.06.2018.
 */
class ProfilePostGridRecyclerAdapter(var kullaniciPostlari:ArrayList<UserPosts>, var myContext:Context): RecyclerView.Adapter<ProfilePostGridRecyclerAdapter.MyViewHolder>() {

    lateinit var inflater:LayoutInflater

    init {
        Collections.sort(kullaniciPostlari, object : Comparator<UserPosts> {
            override fun compare(o1: UserPosts?, o2: UserPosts?): Int {
                if (o1!!.postYuklenmeTarih!! > o2!!.postYuklenmeTarih!!) {
                    return -1
                } else return 1
            }
        })

    }

    init {
        inflater= LayoutInflater.from(myContext)
    }

    override fun getItemCount(): Int {

        return kullaniciPostlari.size

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            var tekSutunDosya=inflater.inflate(R.layout.tek_sutun_grid_resim_profil, parent, false)
            return MyViewHolder(tekSutunDosya)

    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var dosyaYolu= kullaniciPostlari.get(position).postURL
        var noktaninGectigiIndex=dosyaYolu!!.lastIndexOf(".")
        var dosyaTuru=dosyaYolu!!.substring(noktaninGectigiIndex, noktaninGectigiIndex+4)


        if(dosyaTuru.equals(".mp4")){
            //Log.e("DOSYA TURU","DOSYA TURU"+ dosyaTuru)
            holder.videoIcon.visibility=View.VISIBLE

            VideodanThumbOlustur(holder).execute(dosyaYolu)

            holder.setData(kullaniciPostlari.get(position), true)

        }else {
            //Log.e("DOSYA TURU","DOSYA TURU"+ dosyaTuru)
            holder.videoIcon.visibility=View.GONE
            holder.dosyaProgressBar.visibility=View.VISIBLE
            UniversalImageLoader.setImage(dosyaYolu!!, holder.dosyaResim, holder.dosyaProgressBar,"")
            holder.setData(kullaniciPostlari.get(position), false)
        }

    }

    class VideodanThumbOlustur(var holder: MyViewHolder) : AsyncTask<String,Void,Bitmap>(){

        override fun onPreExecute() {
            super.onPreExecute()

            holder.dosyaProgressBar.visibility=View.VISIBLE
        }

        override fun doInBackground(vararg p0: String?): Bitmap? {

            if(p0[0] != null){
                var videoPath=p0[0]

                var bitmap: Bitmap? = null
                var mediaMetadataRetriever: MediaMetadataRetriever? = null
                try {
                    mediaMetadataRetriever = MediaMetadataRetriever()
                    if (Build.VERSION.SDK_INT >= 14)
                        mediaMetadataRetriever.setDataSource(videoPath, HashMap())
                    else
                        mediaMetadataRetriever.setDataSource(videoPath)

                    bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw Throwable(
                            "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)

                } finally {
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release()
                    }else{
                        return null
                    }
                }
                if (bitmap != null)
                return bitmap
                else return null
            }

            return null

        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            holder.dosyaProgressBar.visibility=View.GONE
            if(result != null)
            holder.dosyaResim.setImageBitmap(result)
        }

    }



     inner  class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {


        var tekSutunDosya=itemView as ConstraintLayout
        var videoIcon=tekSutunDosya.imgVideoIcon
        var dosyaResim=tekSutunDosya.imgTekSutunImage
        var dosyaProgressBar=tekSutunDosya.progressBar


        fun setData(oankiGonderi: UserPosts, videoMu: Boolean) {

            //bu kısım calısıyorsa bu adapter begendigim gönderiler için veya profilesetting activityde kullanılmıstır
            tekSutunDosya.setOnClickListener {
                if(myContext is ProfileSettingsActivity){
                    (myContext as ProfileSettingsActivity).profileSettingsRoot.visibility= View.GONE
                    (myContext as ProfileSettingsActivity).profileSettingsContainer.visibility=View.VISIBLE
                    EventBus.getDefault().postSticky(EventbusDataEvents.SecilenGonderiyiGonder(oankiGonderi, videoMu))
                    var transaction=(myContext as ProfileSettingsActivity).supportFragmentManager.beginTransaction()
                    transaction.hide((myContext as ProfileSettingsActivity).supportFragmentManager.findFragmentByTag("fra1"))
                    transaction.add(R.id.profileSettingsContainer,TekGonderiFragment(),"fra2")
                    transaction.addToBackStack("TekGonderiFragment")
                    transaction.commit()
                }
                //bu kısım calısıyorsa bu adapter search activitydeki populer son paylasımlar için kullanılmıstır
                else if(myContext is SearchActivity){

                    (myContext as SearchActivity).tumLayout.visibility= View.GONE
                    (myContext as SearchActivity).frameLayout.visibility=View.VISIBLE
                    EventBus.getDefault().postSticky(EventbusDataEvents.SecilenGonderiyiGonder(oankiGonderi, videoMu))
                    var transaction=(myContext as SearchActivity).supportFragmentManager.beginTransaction()
                    transaction.add(R.id.frameLayout,TekGonderiSearchFragment(),"fra2")
                    transaction.addToBackStack("TekGonderiSearchFragment")
                    transaction.commit()


                }
                //bu kısım calısıyorsa bu adapter profile activity veya user profile activiyde kullanılmıstır
                else {
                    //Log.e("ZZZ","Secilen post :"+oankiGonderi.postURL+" video mu:"+videoMu)
                    (myContext as AppCompatActivity).tumlayout.visibility= View.INVISIBLE
                    (myContext as AppCompatActivity).profileContainer.visibility=View.VISIBLE
                    EventBus.getDefault().postSticky(EventbusDataEvents.SecilenGonderiyiGonder(oankiGonderi, videoMu))
                    var transaction=(myContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                    transaction.add(R.id.profileContainer,TekGonderiFragment(),"fra2")
                    transaction.addToBackStack("tekGonderiFragmentEklendi")
                    transaction.commit()
                }
            }




        }


    }




}