package com.ssafy.ccd.src.main.calender

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.src.dto.Pet
import java.lang.Exception

private const val TAG = "CalendarWritePetAdapter"
class CalendarWritePetAdapter():RecyclerView.Adapter<CalendarWritePetAdapter.PetViewHolder>() {
    var list = mutableListOf<Pet>()
    var selectItem = -1

    inner class PetViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


        fun bind(data: Pet){
            if(selectItem == position){
                itemView.findViewById<LottieAnimationView>(R.id.fragment_calendar_pets_check).visibility = View.VISIBLE
            }else itemView.findViewById<LottieAnimationView>(R.id.fragment_calendar_pets_check).visibility = View.INVISIBLE

            if(data.photoPath == null || data.photoPath == ""){
                Log.d(TAG, "onBind: this is photoPath null")
                Glide.with(itemView)
                    .load(R.drawable.logo)
                    .circleCrop()
                    .into(itemView.findViewById(R.id.fragment_calender_pets_item))
            }else{
                val storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
                val storageRef = storage.reference
                Log.d(TAG, "onBind: ${data.photoPath}")
                storageRef.child("${data.photoPath}").downloadUrl.addOnSuccessListener { p0 ->
                    Log.d(TAG, "onBind: this is photoPath not null")
                    Glide.with(itemView)
                        .load(p0)
                        .circleCrop()
                        .into(itemView.findViewById(R.id.fragment_calender_pets_item))
                }.addOnFailureListener { p0 -> Log.d(TAG, "onFailure: ${p0.toString()}") }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets,parent,false))
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            var check = false
            itemView.setOnClickListener {
                if(check){
                    //선택됨
                    itemView.findViewById<LottieAnimationView>(R.id.fragment_calendar_pets_check).visibility = View.INVISIBLE
                    check = false
                }else{
                    itemView.findViewById<LottieAnimationView>(R.id.fragment_calendar_pets_check).visibility = View.VISIBLE
                    check = true
                    itemClickListener.onClick(itemView,position,list[position].id)
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    interface ItemClickListener{
        fun onClick(view: View, position: Int, id: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}