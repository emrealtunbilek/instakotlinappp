package com.emrealtunbilek.instakotlinapp.Profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {

    lateinit var circleProfileImageFragment:CircleImageView


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view=inflater!!.inflate(R.layout.fragment_profile_edit, container, false)

        circleProfileImageFragment=view.findViewById(R.id.circleProfileImage)
        initImageLoader()
        setupProfilePicture()


        view.imgClose.setOnClickListener {

            activity.onBackPressed()

        }


        return view
    }

    private fun initImageLoader(){

        var universalImageLoader=UniversalImageLoader(activity)
        ImageLoader.getInstance().init(universalImageLoader.config)

    }

    private fun setupProfilePicture() {

        //https://orig00.deviantart.net/67cd/f/2012/309/8/c/android_icon_by_gabrydesign-d4m7he9.png
        var imgURL= "orig00.deviantart.net/67cd/f/2012/309/8/c/android_icon_by_gabrydesign-d4m7he9.png"
        UniversalImageLoader.setImage(imgURL,circleProfileImageFragment,null,"https://")


    }

}
