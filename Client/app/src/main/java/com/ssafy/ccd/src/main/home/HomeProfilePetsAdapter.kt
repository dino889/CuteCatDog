package com.ssafy.ccd.src.main.home

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.src.dto.Pet
import java.lang.Exception

private const val TAG = "HomeProfilePetsAdapter_ccd"
class HomeProfilePetsAdapter(var list:MutableList<Pet>):RecyclerView.Adapter<HomeProfilePetsAdapter.BaseViewHolder>() {
    val ITEM = 1
    val FOOTER = 2

    open class BaseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    class FooterViewHolder(itemView: View) : BaseViewHolder(itemView)
    class ItemViewHolder(itemView:View) : BaseViewHolder(itemView){
        fun bind(data: Pet){
            if(data.photoPath == null || data.photoPath == ""){
                Glide.with(itemView)
                    .load(R.drawable.logo2)
                    .transform(CenterCrop(),RoundedCorners(40))
                    .into(itemView.findViewById(R.id.fragment_home_ivPetsImg))
            }else{
                var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
                var storageRef = storage.reference
                storageRef.child("${data.photoPath}").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                    override fun onSuccess(p0: Uri?) {
                        Glide.with(itemView)
                            .load(p0)
                            .transform(CenterCrop(),RoundedCorners(40))
                            .into(itemView.findViewById(R.id.fragment_home_ivPetsImg))
                    }

                }).addOnFailureListener(object: OnFailureListener {
                    @SuppressLint("LongLogTag")
                    override fun onFailure(p0: Exception) {
                        Log.d(TAG, "onFailure: ")
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(viewType){
            FOOTER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_pets_footer,parent,false)
                FooterViewHolder(view)
            }
            ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_pets_list,parent,false)
                ItemViewHolder(view)
            }
            else -> throw Exception("$viewType")
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            holder.bind(list[position])
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(it, position, list[position])
            }
        }
        if(holder is FooterViewHolder){
            holder.itemView.findViewById<AppCompatButton>(R.id.fragmetHome_btn_add).setOnClickListener {
                addClickListener.onClick(it,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int =
        when(position){
            list.size -> FOOTER
            else -> ITEM
        }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, pet: Pet)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
    interface  AddClickListener{
        fun onClick(view:View, position:Int)
    }
    private lateinit var addClickListener: AddClickListener
    fun setAddClickListener(addClickListener: AddClickListener){
        this.addClickListener = addClickListener
    }
}