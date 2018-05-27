package com.emrealtunbilek.instakotlinapp.Share


import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.DosyaIslemleri
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.emrealtunbilek.instakotlinapp.utils.ShareActivityGridViewAdapter
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus


class ShareGalleryFragment : Fragment() {

    var secilenResimYolu:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_share_gallery, container, false)

        var klasorPaths=ArrayList<String>()
        var klasorAdlari=ArrayList<String>()

        var root= Environment.getExternalStorageDirectory().path


        var test=root+"/DCIM/TestKlasor"
        var kameraResimleri= root+"/DCIM/Camera"
        var indirilenResimler=root+"/Download"
        var whatsappResimleri=root+"/WhatsApp/Media/WhatsApp Images"

        klasorPaths.add(test)
        klasorPaths.add(kameraResimleri)
        klasorPaths.add(indirilenResimler)
        klasorPaths.add(whatsappResimleri)

        klasorAdlari.add("Test")
        klasorAdlari.add("Kamera")
        klasorAdlari.add("Indirilenler")
        klasorAdlari.add("Whatsapp")

        var spinnerArrayAdapter=ArrayAdapter(activity, android.R.layout.simple_spinner_item, klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnKlasorAdlari.adapter=spinnerArrayAdapter

        //ilk açıldıgında en son dosya gösterilir
        view.spnKlasorAdlari.setSelection(0)

        view.spnKlasorAdlari.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                setupGridView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))


            }

        }


        view.tvIleriButton.setOnClickListener {

            activity!!.anaLayout.visibility= View.GONE
            activity!!.fragmentContainerLayout.visibility=View.VISIBLE
            var transaction=activity!!.supportFragmentManager.beginTransaction()

            EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(secilenResimYolu))

            transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
            transaction.addToBackStack("shareNextFragmentEklendi")
            transaction.commit()



        }




        return view
    }

    fun setupGridView(secilenKlasordekiDosyalar : ArrayList<String>){
        var gridAdapter=ShareActivityGridViewAdapter(activity,R.layout.tek_sutun_grid_resim,secilenKlasordekiDosyalar)

        gridResimler.adapter=gridAdapter

        //ilk açıldıgında ilk dosya gösterilir
        secilenResimYolu=secilenKlasordekiDosyalar.get(0)
        resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(0))

        gridResimler.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               secilenResimYolu= secilenKlasordekiDosyalar.get(position)
               resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(position))
            }

        })

    }

    private fun resimVeyaVideoGoster(dosyaYolu: String) {

        var dosyaTuru=dosyaYolu.substring(dosyaYolu.lastIndexOf("."))
        //file://asdsadasdas.mp4


        if(dosyaTuru != null){
            if(dosyaTuru.equals(".mp4")){

                videoView.visibility=View.VISIBLE
                imgCropView.visibility=View.GONE
                videoView.setVideoURI(Uri.parse("file://"+dosyaYolu))
                Log.e("HATA","Video : "+"file://"+dosyaYolu)
                videoView.start()

            }else {
                videoView.visibility=View.GONE
                imgCropView.visibility=View.VISIBLE
                UniversalImageLoader.setImage(dosyaYolu,imgCropView,null,"file://")

            }
        }







    }

}