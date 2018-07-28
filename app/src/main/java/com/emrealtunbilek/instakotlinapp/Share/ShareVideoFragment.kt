package com.emrealtunbilek.instakotlinapp.Share


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_video.*
import kotlinx.android.synthetic.main.fragment_share_video.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class ShareVideoFragment : Fragment() {

    var videoView:CameraView?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view =inflater.inflate(R.layout.fragment_share_video, container, false)

        videoView=view.videoView

        var olusacakVideoDosyaAdi=System.currentTimeMillis()
        var olusacakVideoDosya= File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/InstagramKotlinApp/compressed/"+olusacakVideoDosyaAdi+".mp4")

        videoView!!.addCameraListener(object : CameraListener(){

            override fun onVideoTaken(video: File?) {
                super.onVideoTaken(video)

                activity!!.anaLayout.visibility= View.GONE
                activity!!.fragmentContainerLayout.visibility=View.VISIBLE
                var transaction=activity!!.supportFragmentManager.beginTransaction()

                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(video!!.absolutePath.toString(),false))
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()

            }

        })



        view.imgVideoCek.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(event!!.action==MotionEvent.ACTION_DOWN){

                    videoView!!.startCapturingVideo(olusacakVideoDosya)
                    Toast.makeText(activity,"Video kaydediliyor",Toast.LENGTH_SHORT).show()
                    return true

                }else if(event!!.action==MotionEvent.ACTION_UP){
                    Toast.makeText(activity,"Video kaydedildi",Toast.LENGTH_SHORT).show()
                    videoView!!.stopCapturingVideo()
                    return true
                }

                return false
            }

        })

        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }


        return view
    }

    override fun onResume() {
        super.onResume()
        //Log.e("HATA2"," VIDEO FRAGMENTI ON RESUME")
        videoView!!.start()
    }

    override fun onPause() {
        super.onPause()
        //Log.e("HATA2"," VIDEO FRAGMENTI ON PAUSE")
       videoView!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.e("HATA2"," VIDEO FRAGMENTI ON DESTROY")
        if(videoView!=null)
        videoView!!.destroy()
    }

}
