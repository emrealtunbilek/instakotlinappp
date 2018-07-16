package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import com.emrealtunbilek.instakotlinapp.Profile.YukleniyorFragment
import com.emrealtunbilek.instakotlinapp.Share.ShareNextFragment
import com.iceteck.silicompressorr.SiliCompressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

                       var noktaninSonIndexi= okunanDosyaYolu.lastIndexOf(".")
                        if(noktaninSonIndexi>0){
                            var dosyaTuru=okunanDosyaYolu.substring(noktaninSonIndexi)

                            Log.e("HATA","okunan dosya türü"+dosyaTuru)

                            if(dosyaTuru!= null && (dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4"))){


                                tumDosyalar.add(okunanDosyaYolu)
                                Log.e("HATA","arrayliste eklenen dosya"+okunanDosyaYolu)
                            }
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

        fun klasorMevcutMu(klasorAdi: String):Boolean{

            var dosya=File(klasorAdi)

            var klasordekiDosyalar=dosya.listFiles()

            if(klasordekiDosyalar != null && klasordekiDosyalar.size>0){
                Log.e("GALERI","KLASOR BOS DEGIL"+klasorAdi)
                return true
            }else return false

        }

        fun galeridekiTumFotograflariGetir(context: Context):ArrayList<String>{
            val galleryImageUrls: ArrayList<String>
            val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)//get all columns of type images
            val orderBy = MediaStore.Images.Media.DATE_TAKEN//order data by date

            val imagecursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy + " DESC")//get all data in Cursor by sorting in DESC order

            galleryImageUrls = ArrayList()

            for (i in 0 until imagecursor.getCount()) {
                imagecursor.moveToPosition(i)
                val dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA)//get column index
                galleryImageUrls.add(imagecursor.getString(dataColumnIndex))//get Image from column index

            }

            return galleryImageUrls
        }

        fun cropImageandSave(bitmap: Bitmap): String {


            var yeniDosyaninAdi="cropimage"+System.currentTimeMillis().toString().substring(8,12)+".jpg"
            Log.e("GALERI","olusan yeni dosyanın adı"+yeniDosyaninAdi)

            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)


            val ExternalStorageDirectory = Environment.getExternalStorageDirectory().path+"/PICTURES/Screenshots"
            val fileKlasor = File(ExternalStorageDirectory)
            val file = File(ExternalStorageDirectory + File.separator + yeniDosyaninAdi)

            var fileOutputStream: FileOutputStream? = null
            try {

                if(fileKlasor.isDirectory || fileKlasor.mkdirs())
                {
                    file.createNewFile()
                    fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(bytes.toByteArray())

                    Log.e("GALERI","yeni dosyanın absolute path:"+file.absolutePath)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        return file.absolutePath
                        fileOutputStream.close()
                    } catch (e: IOException) {

                        e.printStackTrace()
                    }

                }
            }
            return ""

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