package com.emrealtunbilek.instakotlinapp.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.BildirimModel
import com.emrealtunbilek.instakotlinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_satir_gonderi_begendi_news.view.*
import kotlinx.android.synthetic.main.tek_satir_takip_basladi_news.view.*
import kotlinx.android.synthetic.main.tek_satir_takip_istegi_news.view.*
import java.util.*

/**
 * Created by Emre on 8.07.2018.
 */
class SenNewsRecyclerAdapter(var context: Context, var tumBildirimler: ArrayList<BildirimModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        Collections.sort(tumBildirimler, object : Comparator<BildirimModel> {
            override fun compare(o1: BildirimModel?, o2: BildirimModel?): Int {
                if (o1!!.time!! > o2!!.time!!) {
                    return -1
                } else return 1
            }
        })

    }

    override fun getItemViewType(position: Int): Int {

        when (tumBildirimler.get(position).bildirim_tur) {

            Bildirimler.GONDERI_BEGENILDI -> {
                return Bildirimler.GONDERI_BEGENILDI
            }
            Bildirimler.TAKIP_ETMEYE_BASLADI -> {
                return Bildirimler.TAKIP_ETMEYE_BASLADI
            }
            Bildirimler.YENI_TAKIP_ISTEGI -> {
                return Bildirimler.YENI_TAKIP_ISTEGI
            }
            else -> return 0

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType==Bildirimler.GONDERI_BEGENILDI){

            var myView=LayoutInflater.from(context).inflate(R.layout.tek_satir_gonderi_begendi_news,parent,false)

            return GonderiBegendiViewHolder(myView)


        }else if(viewType==Bildirimler.TAKIP_ETMEYE_BASLADI){

            var myView=LayoutInflater.from(context).inflate(R.layout.tek_satir_takip_basladi_news,parent,false)

            return TakipBasladiViewHolder(myView)

        }else{
            var myView=LayoutInflater.from(context).inflate(R.layout.tek_satir_takip_istegi_news,parent,false)

            return TakipIstekViewHolder(myView)

        }

    }

    override fun getItemCount(): Int {
        return tumBildirimler.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (tumBildirimler.get(position).bildirim_tur) {

            Bildirimler.GONDERI_BEGENILDI -> {
                (holder as GonderiBegendiViewHolder).setData(tumBildirimler.get(position))
            }
            Bildirimler.TAKIP_ETMEYE_BASLADI -> {
                (holder as TakipBasladiViewHolder).setData(tumBildirimler.get(position))
            }
            Bildirimler.YENI_TAKIP_ISTEGI -> {
                (holder as TakipIstekViewHolder).setData(tumBildirimler.get(position))
            }


        }

    }

    class TakipIstekViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumLayout=itemView as ConstraintLayout
        var takipEdenUserProfileResim=tumLayout.imgTakipEdenUserPicture
        var takipEdenUserName=tumLayout.tvTakipEdenUserName
        var takipEdenUserAdiSoyadi=tumLayout.tvTakipEdenAdiSoyadi
        var istekOnaylaButonu=tumLayout.btnOnayla
        var istekSilButonu = tumLayout.btnSil

        fun setData(oankiBildirim: BildirimModel) {

           idsiVerilenKullanicininBilgileri(oankiBildirim.user_id)

            istekOnaylaButonu.setOnClickListener {

            }

            istekSilButonu.setOnClickListener {

            }


        }

        private fun idsiVerilenKullanicininBilgileri(user_id: String?) {

            FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if(p0!!.getValue()!=null){

                        if(!p0!!.child("user_name").getValue().toString().isNullOrEmpty())
                        takipEdenUserName.setText(p0!!.child("user_name").getValue().toString())

                        if(!p0!!.child("adi_soyadi").getValue().toString().isNullOrEmpty())
                        takipEdenUserAdiSoyadi.setText(p0!!.child("adi_soyadi").getValue().toString())

                        if(!p0!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()){
                            var takipEdenPicURL=p0!!.child("user_detail").child("profile_picture").getValue().toString()
                            UniversalImageLoader.setImage(takipEdenPicURL,takipEdenUserProfileResim,null,"")
                        }else {
                            var takipEdenPicURL="https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                            UniversalImageLoader.setImage(takipEdenPicURL,takipEdenUserProfileResim,null,"")
                        }


                    }


                }

            })

        }


    }

    class TakipBasladiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tumLayout=itemView as ConstraintLayout
        var takipEdenUserPicture=tumLayout.imgTakipEdenUserPic
        var bildirim=tumLayout.tvBildirimTakipBasladi


        fun setData(oankiBildirim: BildirimModel) {

            idsiVerilenKullanicininBilgileri(oankiBildirim.user_id,oankiBildirim.time!!)

        }

        private fun idsiVerilenKullanicininBilgileri(user_id: String?,bildirimZamani:Long) {

            FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if(p0!!.getValue()!=null){

                        if(!p0!!.child("user_name").getValue().toString().isNullOrEmpty())
                            bildirim.setText(p0!!.child("user_name").getValue().toString()+" sizi takip etmeye başladı.   "+TimeAgo.getTimeAgoForComments(bildirimZamani))



                        if(!p0!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()){
                            var takipEdenPicURL=p0!!.child("user_detail").child("profile_picture").getValue().toString()
                            UniversalImageLoader.setImage(takipEdenPicURL,takipEdenUserPicture,null,"")
                        }else {
                            var takipEdenPicURL="https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                            UniversalImageLoader.setImage(takipEdenPicURL,takipEdenUserPicture,null,"")
                        }


                    }


                }

            })

        }


    }

    class GonderiBegendiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tumLayout=itemView as ConstraintLayout
        var begenenProfilePicture=tumLayout.imgBegenenProfilePicture
        var bildirimGonderibegenildi = tumLayout.tvBildirimGonderiBegen
        var begenilenGonderiPicture=tumLayout.imgBegenilenGonderi



        fun setData(oankiBildirim: BildirimModel) {

            idsiVerilenKullanicininBilgileri(oankiBildirim.user_id, oankiBildirim.gonderi_id, oankiBildirim.time!!)

        }

        private fun idsiVerilenKullanicininBilgileri(user_id: String?, gonderi_id:String?,bildirimZamani:Long) {

            FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if(p0!!.getValue()!=null){

                        var userName=p0!!.child("user_name").getValue().toString()
                        if(!p0!!.child("user_name").getValue().toString().isNullOrEmpty())
                            bildirimGonderibegenildi.setText(userName+" fotoğrafını beğendi.  "+TimeAgo.getTimeAgoForComments(bildirimZamani))



                        if(!p0!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()){
                            var takipEdenPicURL=p0!!.child("user_detail").child("profile_picture").getValue().toString()
                            UniversalImageLoader.setImage(takipEdenPicURL,begenenProfilePicture,null,"")
                        }else {
                            var takipEdenPicURL="https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                            UniversalImageLoader.setImage(takipEdenPicURL,begenenProfilePicture,null,"")
                        }


                        FirebaseDatabase.getInstance().getReference().child("posts").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(gonderi_id).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                                if(p0!!.getValue()!=null){

                                    var dosyaYolu=p0!!.child("file_url").getValue().toString()
                                    var dosyaTuru=dosyaYolu!!.substring(dosyaYolu.lastIndexOf("."), dosyaYolu.lastIndexOf(".")+4)

                                    if(dosyaTuru.equals(".mp4")) {
                                        bildirimGonderibegenildi.setText(userName+" videonu beğendi.  "+TimeAgo.getTimeAgoForComments(bildirimZamani))
                                        begenilenGonderiPicture.visibility=View.GONE

                                    }else {

                                        if(!p0!!.child("file_url").getValue().toString().isNullOrEmpty()){
                                            begenilenGonderiPicture.visibility=View.VISIBLE
                                            var begenilenFotoURL=p0!!.child("file_url").getValue().toString()
                                            UniversalImageLoader.setImage(begenilenFotoURL,begenilenGonderiPicture,null,"")
                                        }else {
                                            begenilenGonderiPicture.visibility=View.VISIBLE
                                            var begenilenFotoURL="https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                            UniversalImageLoader.setImage(begenilenFotoURL,begenilenGonderiPicture,null,"")
                                        }

                                    }


                                }
                            }


                        })



                    }


                }

            })

        }
    }


}