package com.ssafy.ccd.src.main.mypage

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.ItemCalenderPetsBinding
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.dto.User
import retrofit2.http.HEAD
import java.lang.Exception

private const val TAG = "PetListRecyclerviewAdap"
class PetListRecyclerviewAdapter(val user: User) : RecyclerView.Adapter<PetListRecyclerviewAdapter.BaseViewHolder>() {
    val FOOTER = 2
    val ITEM = 1
    val HEADER = 0
    var petList = mutableListOf<Pet>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            when(viewType){
                HEADER -> {
                    HeaderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_calender_pets, parent,false))
                }
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
//        if(position == petList.size + 1) {
            if(holder is FooterViewHolder){
                holder.itemView.findViewById<ImageButton>(R.id.fragment_calender_add_petsBtn).setOnClickListener {
                    addClickListener.onClick(it,position)
                }
            }
//        } else {
            else if(holder is ItemViewHolder){
                holder.onBind(petList[position - 1])
                holder.itemView.setOnClickListener {
                    Log.d(TAG, "onBindViewHolder: itemView")
                    itemClickListener.onClick(it,position, petList[position - 1])
                }
            } else if(holder is HeaderViewHolder) {
                holder.onBind(user)
                holder.itemView.setOnClickListener {
                    userInfoClickListener.onClick(it)
                }
            }
//        }
    }

    override fun getItemCount(): Int {
        return petList.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == petList.size + 1) {
            FOOTER
        } else if(position == 0) {
            HEADER
        } else {
            ITEM
        }
    }

    open class BaseViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)

    class FooterViewHolder(itemView:View):BaseViewHolder(itemView)

    class HeaderViewHolder(private val binding: ItemCalenderPetsBinding):BaseViewHolder(binding.root) {
        fun onBind(user: User) {
            binding.user = user
            binding.executePendingBindings()
//            if(user.profileImage == null || user.profileImage == ""){
//                Log.d(TAG, "onBind: this is photoPath null")
//                Glide.with(itemView)
//                    .load(R.drawable.logo)
//                    .circleCrop()
//                    .into(itemView.findViewById(R.id.fragment_calender_pets_item))
//            }else{
//                val storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
//                val storageRef = storage.reference
//                storageRef.child("${user.profileImage}").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
//                    override fun onSuccess(p0: Uri?) {
//                        Log.d(TAG, "onBind: this is photoPath not null")
//                        Glide.with(itemView)
//                            .load(p0)
//                            .circleCrop()
//                            .into(itemView.findViewById(R.id.fragment_calender_pets_item))
//                    }
//
//                }).addOnFailureListener(object: OnFailureListener {
//                    override fun onFailure(p0: Exception) {
//                        Log.d(TAG, "onFailure: ${p0.toString()}")
//                    }
//                })
//            }
        }
    }

    class ItemViewHolder(itemView:View):BaseViewHolder(itemView) {
        fun onBind(data:Pet){

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

    interface AddClickListener{
        fun onClick(view:View, position:Int)
    }

    private lateinit var addClickListener: AddClickListener

    fun setAddClickListener(addClickListener: AddClickListener){
        this.addClickListener = addClickListener
    }

    interface  UserInfoClickListener{
        fun onClick(view:View)
    }

    private lateinit var userInfoClickListener: UserInfoClickListener

    fun setUserInfoClickListener(userInfoClickListener: UserInfoClickListener){
        this.userInfoClickListener = userInfoClickListener
    }
}