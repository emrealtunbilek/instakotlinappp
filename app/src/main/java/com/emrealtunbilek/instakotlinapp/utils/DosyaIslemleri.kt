package com.emrealtunbilek.instakotlinapp.utils

import android.util.Log
import java.io.File

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

                        if(dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4")){


                            tumDosyalar.add(okunanDosyaYolu)
                            Log.e("HATA","arrayliste eklenen dosya"+okunanDosyaYolu)
                        }


                    }

                }

            }

            return tumDosyalar

        }


    }

}