package com.emrealtunbilek.instakotlinapp.Share


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R
import com.otaliastudios.cameraview.CameraView
import kotlinx.android.synthetic.main.fragment_share_video.view.*


class ShareVideoFragment : Fragment() {

    lateinit var videoView:CameraView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view =inflater.inflate(R.layout.fragment_share_video, container, false)

        videoView=view.videoView

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("HATA2"," VIDEO FRAGMENTI ON RESUME")
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        Log.e("HATA2"," VIDEO FRAGMENTI ON PAUSE")
       videoView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HATA2"," VIDEO FRAGMENTI ON DESTROY")
        videoView.destroy()
    }

}
