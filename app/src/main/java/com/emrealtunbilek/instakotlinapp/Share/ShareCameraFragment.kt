package com.emrealtunbilek.instakotlinapp.Share


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_camera.*
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ShareCameraFragment : Fragment() {


     var cameraView:CameraView?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view=inflater.inflate(R.layout.fragment_share_camera, container, false)

        cameraView=view.videoView
        cameraView!!.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraView!!.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)

        view.imgFotoCek.setOnClickListener {
            cameraView!!.capturePicture()
        }

        cameraView!!.addCameraListener(object : CameraListener(){

            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)

                var cekilenFotoAdi=System.currentTimeMillis()
                var cekilenFoto=File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/InstagramKotlinApp/compressed/"+cekilenFotoAdi+".jpg")

                var dosyaOlustur=FileOutputStream(cekilenFoto)
                dosyaOlustur.write(jpeg)
                dosyaOlustur.close()

                activity!!.anaLayout.visibility= View.GONE
                activity!!.fragmentContainerLayout.visibility=View.VISIBLE
                var transaction=activity!!.supportFragmentManager.beginTransaction()

                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(cekilenFoto.absolutePath.toString(),true))
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()


                Log.e("HATA2","cekilen resim buraya kaydedildi :"+cekilenFoto.absolutePath.toString())



            }

        })

        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }


        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("HATA2"," CAMERA FRAGMENTI ON RESUME")
        cameraView!!.start()
    }

    override fun onPause() {
        super.onPause()
        Log.e("HATA2"," CAMERA FRAGMENTI ON PAUSE")
        cameraView!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HATA2"," CAMERA FRAGMENTI ON DESTROY")

        if(cameraView!=null)
         cameraView!!.destroy()
    }

}
