package com.emrealtunbilek.instakotlinapp.Share


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ShareGalleryFragment : Fragment() {

    var secilenDosyaYolu:String?=null
    var dosyaTuruResimMi : Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_share_gallery, container, false)

        var klasorPaths=ArrayList<String>()
        var klasorAdlari=ArrayList<String>()

        var root= Environment.getExternalStorageDirectory().path


        var kameraResimleri= root+"/DCIM/Camera"
        var indirilenResimler=root+"/Download"
        var whatsappResimleri=root+"/WhatsApp/Media/WhatsApp Images"
        var screenShot=root+"/PICTURES/Screenshots"
        var twitter=root+"/PICTURES/Twitter"
        var buUygulama = root+"/DCIM/InstagramKotlinApp/compressed"

        klasorAdlari.add("Galeri")

        if(DosyaIslemleri.klasorMevcutMu(kameraResimleri)){
            klasorPaths.add(kameraResimleri)
            klasorAdlari.add("Kamera")
        }
        if(DosyaIslemleri.klasorMevcutMu(indirilenResimler)){
            klasorPaths.add(indirilenResimler)
            klasorAdlari.add("Indirilenler")
        }
        if(DosyaIslemleri.klasorMevcutMu(whatsappResimleri)){
            klasorPaths.add(whatsappResimleri)
            klasorAdlari.add("Whatsapp")
        }
        if(DosyaIslemleri.klasorMevcutMu(screenShot)){
            klasorPaths.add(screenShot)
            klasorAdlari.add("Ekran Alıntıları")
        }
        if(DosyaIslemleri.klasorMevcutMu(twitter)){
            klasorPaths.add(twitter)
            klasorAdlari.add("Twitter")
        }
        if(DosyaIslemleri.klasorMevcutMu(buUygulama)){
            klasorPaths.add(buUygulama)
            klasorAdlari.add("InstaKotlin App")
        }


        var spinnerArrayAdapter=ArrayAdapter(activity, android.R.layout.simple_spinner_item, klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnKlasorAdlari.adapter=spinnerArrayAdapter

        //ilk açıldıgında en son dosya gösterilir
        view.spnKlasorAdlari.setSelection(0)


        view.spnKlasorAdlari.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

               // setupGridView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))

                if(position==0){
                    setupRecyclerView(DosyaIslemleri.galeridekiTumFotograflariGetir(activity!!))
                }else{
                    setupRecyclerView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position-1)))
                }



            }

        }


        view.tvIleriButton.setOnClickListener {

            activity!!.anaLayout.visibility= View.GONE
            activity!!.fragmentContainerLayout.visibility=View.VISIBLE
            var transaction=activity!!.supportFragmentManager.beginTransaction()

            if(dosyaTuruResimMi==true){

                var bitmap=imgCropView.croppedImage
                if(bitmap != null){
                    Log.e("GALERI","BİTMAP OLUSMUS")
                    var croppedImagePath=DosyaIslemleri.cropImageandSave(bitmap)
                    EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(croppedImagePath,dosyaTuruResimMi))
                    transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                    transaction.addToBackStack("shareNextFragmentEklendi")
                    transaction.commit()
                }else{
                    Log.e("GALERI","BİTMAP olusmamıs")
                }



            }else {
                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(secilenDosyaYolu,dosyaTuruResimMi))
                videoView.stopPlayback()
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()

            }





        }

        view.imgClose.setOnClickListener {

            activity!!.onBackPressed()

        }




        return view
    }

    private fun setupRecyclerView(klasordekiDosyalar: ArrayList<String>) {

        var recyclerViewAdapter=ShareGalleryRecyclerAdapter(klasordekiDosyalar, this.activity!!)
        recyclerViewDosyalar.adapter=recyclerViewAdapter

        var layoutManager=GridLayoutManager(this.activity,4)
        recyclerViewDosyalar.layoutManager=layoutManager!!

        recyclerViewDosyalar.setHasFixedSize(true);
        recyclerViewDosyalar.setItemViewCacheSize(30);
        recyclerViewDosyalar.setDrawingCacheEnabled(true);
        recyclerViewDosyalar.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        //ilk açıldıgında ilk dosya gösterilir
        secilenDosyaYolu=klasordekiDosyalar.get(0)
        resimVeyaVideoGoster(secilenDosyaYolu!!)


    }
/*
    fun setupGridView(secilenKlasordekiDosyalar : ArrayList<String>){
        var gridAdapter=ShareActivityGridViewAdapter(activity,R.layout.tek_sutun_grid_resim,secilenKlasordekiDosyalar)

        recyclerViewDosyalar.adapter=gridAdapter

        //ilk açıldıgında ilk dosya gösterilir
        secilenDosyaYolu=secilenKlasordekiDosyalar.get(0)
        resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(0))

        recyclerViewDosyalar.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               secilenDosyaYolu= secilenKlasordekiDosyalar.get(position)
               resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(position))
            }

        })

    }*/

    private fun resimVeyaVideoGoster(dosyaYolu: String) {

        var dosyaTuru=dosyaYolu.substring(dosyaYolu.lastIndexOf("."))
        //file://asdsadasdas.mp4


        if(dosyaTuru != null){
            if(dosyaTuru.equals(".mp4")){

                videoView.visibility=View.VISIBLE
                imgCropView.visibility=View.GONE
                dosyaTuruResimMi=false
                videoView.setVideoURI(Uri.parse("file://"+dosyaYolu))
                Log.e("HATA","Video : "+"file://"+dosyaYolu)
                videoView.start()

            }else {
                videoView.visibility=View.GONE
                imgCropView.visibility=View.VISIBLE
                dosyaTuruResimMi=true
                UniversalImageLoader.setImage(dosyaYolu,imgCropView,null,"file://")

            }
        }







    }

    override fun onResume() {
        super.onResume()
        Log.e("HATA2"," GALERY FRAGMENTI ON RESUME")

    }

    override fun onPause() {
        super.onPause()
        Log.e("HATA2"," GALERY FRAGMENTI ON PAUSE")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HATA2"," GALERY FRAGMENTI ON DESTROY")
    }

    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe
    internal fun onSecilenDosyaEvent(secilenDosya: EventbusDataEvents.GalerySecilenDosyaYolunuGonder) {
        secilenDosyaYolu=secilenDosya!!.dosyaYolu

        resimVeyaVideoGoster(secilenDosyaYolu!!)

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}