package com.emrealtunbilek.instakotlinapp.Home

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Emre on 28.02.2018.
 */
class CameraFragment : Fragment() {

    var myCamera:CameraView?=null
    var kameraIzniVerildiMi=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view=inflater?.inflate(R.layout.fragment_camera, container, false)

        myCamera=view!!.camera_view
        myCamera!!.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        myCamera!!.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)


        myCamera!!.addCameraListener(object : CameraListener(){

            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)

                var cekilenFotoAdi=System.currentTimeMillis()
                var cekilenFotoKlasor= File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/InstagramKotlinApp/compressed/")

                if(cekilenFotoKlasor.isDirectory || cekilenFotoKlasor.mkdirs()){
                    var dosyaTamYolu=File(Environment.getExternalStorageDirectory().absolutePath +"/DCIM/InstagramKotlinApp/compressed/"+cekilenFotoAdi+".jpg")
                    var dosyaOlustur= FileOutputStream(dosyaTamYolu)
                    dosyaOlustur.write(jpeg)
                    Log.e("HATA2","cekilen resim buraya kaydedildi :"+dosyaTamYolu.absolutePath.toString())
                    dosyaOlustur.close()

                }







            }


        })

        view.imgCameraSwitch.setOnClickListener {

            if(myCamera!!.facing==Facing.BACK){
                myCamera!!.facing=Facing.FRONT
            }else {
                myCamera!!.facing=Facing.BACK
            }

        }

        view.imgFotoCek.setOnClickListener {

            if(myCamera!!.facing==Facing.BACK){
                myCamera!!.capturePicture()
            }else {
                myCamera!!.captureSnapshot()
            }



        }


        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("HATA2"," CAMERA FRAGMENTI ON RESUME")
        if(kameraIzniVerildiMi==true)
        myCamera!!.start()
    }

    override fun onPause() {
        super.onPause()
        Log.e("HATA2"," CAMERA FRAGMENTI ON PAUSE")
        myCamera!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HATA2"," CAMERA FRAGMENTI ON DESTROY")

        if(myCamera!=null)
            myCamera!!.destroy()
    }

    @Subscribe(sticky = true)
    internal fun onKameraIzinEvent(izinDurumu: EventbusDataEvents.KameraIzinBilgisiGonder) {
        kameraIzniVerildiMi=izinDurumu.kameraIzniVerildiMi!!
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