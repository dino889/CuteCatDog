package com.ssafy.ccd.src.main.mypage

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import java.lang.Exception

private const val TAG = "PetListRecyclerviewAdap"
class PetListRecyclerviewAdapter() : RecyclerView.Adapter<PetListRecyclerviewAdapter.BaseViewHolder>() {
    val FOOTER = 2
    val ITEM = 1
    var petList = mutableListOf<Pet>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            when(viewType){
                FOOTER -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets_footer,parent,false)
                    FooterViewHolder(view)
                }
                ITEM -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets,parent,false)
                    ItemViewHolder(view)
                }
                else -> throw Exception("$viewType")

            }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            holder.onBind(petList[position-1])
            holder.itemView.setOnClickListener {
                Log.d(TAG, "onBindViewHolder: itemView")
                        itemClickListener.onClick(it,position-1, petList[position-1])
            }
        }
        if(holder is FooterViewHolder){
            holder.itemView.findViewById<ImageButton>(R.id.fragment_calender_add_petsBtn).setOnClickListener {
                addClickListener.onClick(it,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return petList.size + 1
    }

    override fun getItemViewType(position: Int): Int =
        when(position){
            0 -> FOOTER
//            petList.size+1 -> FOOTER
            else -> ITEM
        }
    open class BaseViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    class FooterViewHolder(itemView:View):BaseViewHolder(itemView)
    class ItemViewHolder(itemView:View):BaseViewHolder(itemView) {
        fun onBind(data:Pet){

            if(data.photoPath == null || data.photoPath == ""){
                Log.d(TAG, "onBind: this is photoPath null")
                Glide.with(itemView)
                    .load(R.drawable.logo)
                    .circleCrop()
                    .into(itemView.findViewById(R.id.fragment_calender_pets_item))
            }else{
                var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
                var storageRef = storage.reference
                Log.d(TAG, "onBind: ${data.photoPath}")
                storageRef.child("${data.photoPath}").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                    override fun onSuccess(p0: Uri?) {
                        Log.d(TAG, "onBind: this is photoPath not null")
                        Glide.with(itemView)
                            .load(p0)
                            .circleCrop()
                            .into(itemView.findViewById(R.id.fragment_calender_pets_item))
                    }

                }).addOnFailureListener(object: OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        Log.d(TAG, "onFailure: ${p0.toString()}")
                    }
                })
            }

        }
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