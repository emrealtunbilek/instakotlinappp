package com.emrealtunbilek.instakotlinapp.Generic


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.instakotlinapp.Models.Comments
import com.emrealtunbilek.instakotlinapp.Models.Users

import com.emrealtunbilek.instakotlinapp.R
import com.emrealtunbilek.instakotlinapp.utils.EventbusDataEvents
import com.emrealtunbilek.instakotlinapp.utils.TimeAgo
import com.emrealtunbilek.instakotlinapp.utils.UniversalImageLoader
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_comment.view.*
import kotlinx.android.synthetic.main.tek_satir_comment_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class CommentFragment : Fragment() {

    var yorumYapilacakGonderininID:String?=null
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var myAdapter: FirebaseRecyclerAdapter<Comments,CommentViewHolder>



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_comment, container, false)

        mAuth= FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference.child("comments").child(yorumYapilacakGonderininID)


        val options = FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(mRef, Comments::class.java)
                .build()

        myAdapter=object : FirebaseRecyclerAdapter<Comments,CommentViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                var commentViewHolder=inflater.inflate(R.layout.tek_satir_comment_item, parent, false)

                return CommentViewHolder(commentViewHolder)
            }

            override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comments) {
                holder.setData(model)
            }

        }

        view.yorumlarRecyclerView.adapter=myAdapter
        view.yorumlarRecyclerView.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)



        return view
    }

    class CommentViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tumCommentLayoutu=itemView as ConstraintLayout
        var yorumYapanUserPhoto=tumCommentLayoutu.yorumYapanUserProfile
        var kullaniciAdiveYorum=tumCommentLayoutu.tvUsernameAndYorum
        var yorumBegen=tumCommentLayoutu.imgBegen
        var yorumSure=tumCommentLayoutu.tvYorumSure
        var yorumBegenmeSayisi=tumCommentLayoutu.tvBegenmeSayisi

        fun setData(oanOlusturulanYorum: Comments) {


            yorumSure.setText(TimeAgo.getTimeAgoForComments(oanOlusturulanYorum!!.yorum_tarih!!))
            yorumBegenmeSayisi.setText(oanOlusturulanYorum.yorum_begeni)

            kullaniciBilgileriniGetir(oanOlusturulanYorum.user_id, oanOlusturulanYorum.yorum)

        }

        private fun kullaniciBilgileriniGetir(user_id: String?, yorum: String?) {

            var mRef=FirebaseDatabase.getInstance().reference
            mRef.child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    var userNameveYorum="<font color=#000>"+ p0!!.getValue(Users::class.java)!!.user_name!!.toString()+"</font>" + " " + yorum
                    var sonuc:Spanned?=null
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        sonuc=Html.fromHtml(userNameveYorum,Html.FROM_HTML_MODE_LEGACY)
                    }else {
                        sonuc=Html.fromHtml(userNameveYorum)
                    }
                    kullaniciAdiveYorum.setText(sonuc)

                    UniversalImageLoader.setImage(p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture!!.toString(),yorumYapanUserPhoto
                    ,null,"")
                }


            })

        }


    }

    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onYorumYapilacakGonderi(gonderi: EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder) {
        yorumYapilacakGonderininID = gonderi!!.gonderiID!!

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }

}
