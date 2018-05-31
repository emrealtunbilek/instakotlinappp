package com.emrealtunbilek.instakotlinapp.utils

import android.os.AsyncTask
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import com.emrealtunbilek.instakotlinapp.Profile.YukleniyorFragment
import com.emrealtunbilek.instakotlinapp.Share.ShareNextFragment
import com.iceteck.silicompressorr.SiliCompressor
import java.io.File
import java.util.*
import kotlin.Comparator

/**
 * Created by Emre on 24.05.2018.
 */
class DosyaIslemleri {

    companion object {

        fun klasordekiDosyalariGetir(klasorAdi:String):ArrayList<String>{

            var tumDosyalar=ArrayList<String>()

            var file=File(klasorAdi)

            //parametre olarak gönderdiğimiz klasordeki tüm dosyalar alınız
            var klasordekiTumDosyalar=file.listFiles()

            //parametre olarak gönderdiğimiz klasor yolunda eleman olup olmadıgı kontrol edildi
            if(klasordekiTumDosyalar != null){

                //galeriden getirilen resimlerin tarihe göre sondan basa listelenmesi
                if(klasordekiTumDosyalar.size>1){

                    Arrays.sort(klasordekiTumDosyalar, object : Comparator<File>{
                        override fun compare(o1: File?, o2: File?): Int {

                            if(o1!!.lastModified() > o2!!.lastModified()){
                                return -1
                            }else return 1

                        }


                    })

                }

                for (i in 0..klasordekiTumDosyalar.size-1){

                    //sadece dosyalara bakılır
                    if(klasordekiTumDosyalar[i].isFile){

                        Log.e("HATA","okunan veri bir dosya")

                        //okudugumuz dosyanın telefondaki yeri ve de adını içerir.
                        //files://root/logo.png
                       var okunanDosyaYolu=klasordekiTumDosyalar[i].absolutePath

                        Log.e("HATA","okunan dosya yolu"+okunanDosyaYolu)

                       var dosyaTuru=okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))

                        Log.e("HATA","okunan dosya türü"+dosyaTuru)

                        if(dosyaTuru!= null && (dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4"))){


                            tumDosyalar.add(okunanDosyaYolu)
                            Log.e("HATA","arrayliste eklenen dosya"+okunanDosyaYolu)
                        }


                    }

                }

            }

            return tumDosyalar

        }

        fun compressResimDosya(fragment: Fragment, secilenResimYolu: String?) {

            ResimCompressAsyncTask(fragment).execute(secilenResimYolu)


        }

        fun compressVideoDosya(fragment: Fragment, secilenDosyaYolu: String) {

            VideoCompressAsyncTask(fragment).execute(secilenDosyaYolu)

        }


    }

    internal class VideoCompressAsyncTask(fragment: Fragment):AsyncTask<String,String,String>(){

        var myFragment=fragment
        var compressFragment=YukleniyorFragment()

        override fun onPreExecute() {
            compressFragment.show(myFragment.activity!!.supportFragmentManager,"compressDialogBasladi")
            compressFragment.isCancelable=false
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String? {

            var yeniOlusanDosyaninKlasoru=File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasor/compressedVideo/")

            if(yeniOlusanDosyaninKlasoru.isDirectory || yeniOlusanDosyaninKlasoru.mkdirs()){
                var yeniDosyaninPath= SiliCompressor.with(myFragment.context).compressVideo(params[0],yeniOlusanDosyaninKlasoru.path)
                return yeniDosyaninPath
            }

            return null

        }

        override fun onPostExecute(yeniDosyaninPath: String?) {

            if(!yeniDosyaninPath.isNullOrEmpty()){

                compressFragment.dismiss()
                (myFragment as ShareNextFragment).uploadStorage(yeniDosyaninPath)


            }

            super.onPostExecute(yeniDosyaninPath)
        }




    }

    internal class ResimCompressAsyncTask(fragment: Fragment):AsyncTask<String,String,String>(){

        var myFragment=fragment
        var compressFragment=YukleniyorFragment()

        override fun onPreExecute() {


            compressFragment.show(myFragment.activity!!.supportFragmentManager,"compressDialogBasladi")
            compressFragment.isCancelable=false
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {

            var yeniOlusanDosyaninKlasoru=File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasor/compressed/")
            var yeniDosyaYolu = SiliCompressor.with(myFragment.context).compress(params[0], yeniOlusanDosyaninKlasoru)

            //sıkıstırılarak olusturulmus yeni  dosyanın yolunu verir
            return yeniDosyaYolu
        }

        override fun onPostExecute(filePath: String?) {

            Log.e("HATA","yENİ DOSYANIN PATHI : "+filePath)
            compressFragment.dismiss()
            (myFragment as ShareNextFragment).uploadStorage(filePath)
            super.onPostExecute(filePath)
        }


    }

}