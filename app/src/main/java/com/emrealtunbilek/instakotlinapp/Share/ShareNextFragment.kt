package com.emrealtunbilek.instakotlinapp.Share


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.emrealtunbilek.instakotlinapp.Models.Posts
import com.emrealtunbilek.instakotlinapp.Profile.YukleniyorFragment

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


class ShareNextFragment : Fragment() {

    var secilenResimYolu:String?=null
    lateinit var photoURI:Uri

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var mStorageReference: StorageReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_share_next, container, false)

        UniversalImageLoader.setImage(secilenResimYolu!!,view!!.imgSecilenResim,null,"file://")

        photoURI= Uri.parse("file://"+secilenResimYolu)

        mAuth= FirebaseAuth.getInstance()
        mUser= mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference
        mStorageReference=FirebaseStorage.getInstance().reference


        view.tvIleriButton.setOnClickListener {

            var dialogYukleniyor= YukleniyorFragment()
            dialogYukleniyor.show(activity!!.supportFragmentManager,"yukleniyorFragmenti")
            dialogYukleniyor.isCancelable=false

            var uploadTask=mStorageReference.child("users").child(mUser.uid).child(photoURI.lastPathSegment).putFile(photoURI)
                    .addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot>{
                        override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                            if(p0!!.isSuccessful){
                                dialogYukleniyor.dismiss()
                                veritabaninaBilgileriYaz(p0!!.getResult().downloadUrl.toString())
                            }
                        }

                    })
                    .addOnFailureListener(object : OnFailureListener{
                        override fun onFailure(p0: Exception) {
                           Toast.makeText(activity,"Hata oluştu"+p0!!.message,Toast.LENGTH_SHORT).show()
                        }

                    })





        }

        return view
    }

    private fun veritabaninaBilgileriYaz(yuklenenFotoURL: String) {

        var postID= mRef.child("posts").child(mUser.uid).push().key
        var yuklenenPost=Posts(mUser.uid,postID,"",etPostAciklama.text.toString(),yuklenenFotoURL)


        mRef.child("posts").child(mUser.uid).child(postID).setValue(yuklenenPost)
        mRef.child("posts").child(mUser.uid).child(postID).child("yuklenme_tarih").setValue(ServerValue.TIMESTAMP) //2424564564

    }



    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onSecilenResimEvent(secilenResim: EventbusDataEvents.PaylasilacakResmiGonder) {
        secilenResimYolu = secilenResim!!.resimYolu!!
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
