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
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * Created by Emre on 13.07.2018.
 */
class TakipNewsRecyclerAdapter(var context: Context, var takipcileriminTumBildirimleri: ArrayList<BildirimModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var filtrelenmisListe=ArrayList<BildirimModel>()
    init {
        Collections.sort(takipcileriminTumBildirimleri, object : Comparator<BildirimModel> {
            override fun compare(p0: BildirimModel?, p1: BildirimModel?): Int {
                if (p0!!.time!! > p1!!.time!!) {
                    return -1
                } else return 1
            }
        })

        for (item in takipcileriminTumBildirimleri){
            if(!item.user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                filtrelenmisListe.add(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == Bildirimler.GONDERI_BEGENILDI) {
            var myView = LayoutInflater.from(context).inflate(R.layout.tek_satir_gonderi_begendi_news, parent, false)

            return GonderiBegendiViewHolder(myView)
        } else {

            var myView = LayoutInflater.from(context).inflate(R.layout.tek_satir_takip_basladi_news, parent, false)

            return TakipBasladiViewHolder(myView)

        }

    }

    override fun getItemViewType(position: Int): Int {
        var oankiBildirim=filtrelenmisListe.get(position)
        if (oankiBildirim.bildirim_tur == Bildirimler.GONDERI_BEGENILDI) {

            return Bildirimler.GONDERI_BEGENILDI

        } else if (oankiBildirim.bildirim_tur == Bildirimler.TAKIP_ETMEYE_BASLADI) {
            return Bildirimler.TAKIP_ETMEYE_BASLADI
        } else return 0
    }

    override fun getItemCount(): Int {
        return filtrelenmisListe.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var oankiBildirim = filtrelenmisListe.get(position)

        if (oankiBildirim.bildirim_tur == Bildirimler.GONDERI_BEGENILDI) {

            (holder as GonderiBegendiViewHolder).setData(oankiBildirim)

        } else {
            (holder as TakipBasladiViewHolder).setData(oankiBildirim)
        }


    }


    inner class TakipBasladiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumLayout = itemView as ConstraintLayout
        var takipEttigimUserProfilpic = tumLayout.imgTakipEdenUserPic
        var bildirim = tumLayout.tvBildirimTakipBasladi


        fun setData(oankiBildirim: BildirimModel) {

            var takipEttigimUserID = oankiBildirim.takip_ettigimin_user_id
            var kimiTakipEtmisUserID = oankiBildirim.user_id
            var kimiTakipEtmisUserName = ""
            var takipEttigimUserName = ""
            var takipEttigimUserProfileURL = ""

            FirebaseDatabase.getInstance().getReference().child("users").child(takipEttigimUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.getValue() != null) {

                        takipEttigimUserName = p0!!.child("user_name").getValue().toString()
                        takipEttigimUserProfileURL = p0!!.child("user_detail").child("profile_picture").getValue().toString()

                        FirebaseDatabase.getInstance().getReference().child("users").child(kimiTakipEtmisUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(p0: DataSnapshot?) {

                                if (p0!!.getValue() != null) {

                                    kimiTakipEtmisUserName = p0!!.child("user_name").getValue().toString()

                                    if (!takipEttigimUserProfileURL.isNullOrEmpty()) {

                                        UniversalImageLoader.setImage(takipEttigimUserProfileURL, takipEttigimUserProfilpic, null, "")
                                    } else {
                                        takipEttigimUserProfileURL = "https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                        UniversalImageLoader.setImage(takipEttigimUserProfileURL, takipEttigimUserProfilpic, null, "")
                                    }

                                    bildirim.setText(takipEttigimUserName + " " + kimiTakipEtmisUserName + " adlı kullanıcıyı takip etmeye başladı "
                                            + TimeAgo.getTimeAgoForComments(oankiBildirim.time!!.toLong()))


                                }

                            }

                        })


                    }
                }

            })

        }

    }

    inner class GonderiBegendiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumLayout = itemView as ConstraintLayout
        var takipEttigimUserProfilpic = tumLayout.imgBegenenProfilePicture
        var bildirimGonderibegenildi = tumLayout.tvBildirimGonderiBegen
        var begenilenGonderiPicture = tumLayout.imgBegenilenGonderi


        fun setData(oankiBildirim: BildirimModel) {

            var takipEttigimUserID = oankiBildirim.takip_ettigimin_user_id
            var kimiTakipEtmisUserID = oankiBildirim.user_id
            var kimiTakipEtmisUserName = ""
            var takipEttigimUserName = ""
            var takipEttigimUserProfileURL = ""
            var gonderiID = oankiBildirim.gonderi_id



                FirebaseDatabase.getInstance().getReference().child("users").child(takipEttigimUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {

                            takipEttigimUserName = p0!!.child("user_name").getValue().toString()
                            takipEttigimUserProfileURL = p0!!.child("user_detail").child("profile_picture").getValue().toString()

                            FirebaseDatabase.getInstance().getReference().child("users").child(kimiTakipEtmisUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {

                                }

                                override fun onDataChange(p0: DataSnapshot?) {

                                    if (p0!!.getValue() != null) {

                                        kimiTakipEtmisUserName = p0!!.child("user_name").getValue().toString()

                                        if (!takipEttigimUserProfileURL.isNullOrEmpty()) {

                                            UniversalImageLoader.setImage(takipEttigimUserProfileURL, takipEttigimUserProfilpic, null, "")
                                        } else {
                                            takipEttigimUserProfileURL = "https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                            UniversalImageLoader.setImage(takipEttigimUserProfileURL, takipEttigimUserProfilpic, null, "")
                                        }


                                        FirebaseDatabase.getInstance().getReference().child("posts").child(kimiTakipEtmisUserID)
                                                .child(gonderiID).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.getValue() != null) {

                                                    var dosyaYolu = p0!!.child("file_url").getValue().toString()
                                                    var dosyaTuru = dosyaYolu!!.substring(dosyaYolu.lastIndexOf("."), dosyaYolu.lastIndexOf(".") + 4)

                                                    if (dosyaTuru.equals(".mp4")) {
                                                        bildirimGonderibegenildi.setText(takipEttigimUserName + " " + kimiTakipEtmisUserName + " kullanıcısının videosunu beğendi. " + TimeAgo.getTimeAgoForComments(oankiBildirim.time!!.toLong()))
                                                        begenilenGonderiPicture.visibility = View.GONE

                                                    } else {

                                                        if (!p0!!.child("file_url").getValue().toString().isNullOrEmpty()) {
                                                            begenilenGonderiPicture.visibility = View.VISIBLE
                                                            bildirimGonderibegenildi.setText(takipEttigimUserName + " " + kimiTakipEtmisUserName + " kullanıcısının fotoğrafını beğendi. " + TimeAgo.getTimeAgoForComments(oankiBildirim.time!!.toLong()))
                                                            var begenilenFotoURL = p0!!.child("file_url").getValue().toString()
                                                            UniversalImageLoader.setImage(begenilenFotoURL, begenilenGonderiPicture, null, "")
                                                        } else {
                                                            begenilenGonderiPicture.visibility = View.VISIBLE
                                                            bildirimGonderibegenildi.setText(takipEttigimUserName + " " + kimiTakipEtmisUserName + " kullanıcısının fotoğrafını beğendi. " + TimeAgo.getTimeAgoForComments(oankiBildirim.time!!.toLong()))
                                                            var begenilenFotoURL = "https://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                                            UniversalImageLoader.setImage(begenilenFotoURL, begenilenGonderiPicture, null, "")
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

                })







        }

    }


}