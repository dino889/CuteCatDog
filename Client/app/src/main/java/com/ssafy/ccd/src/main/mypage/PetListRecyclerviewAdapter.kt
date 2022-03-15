package com.ssafy.ccd.src.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import java.lang.Exception

class PetListRecyclerviewAdapter() : RecyclerView.Adapter<PetListRecyclerviewAdapter.BaseViewHolder>() {
    val FOOTER = 2
    val ITEM = 1
    var petList = mutableListOf<Pet>()

    class BaseViewHolder(val layout:View) : RecyclerView.ViewHolder(layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view =
            when(viewType){
                FOOTER -> {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets_footer,parent,false)
                }
                else -> {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets,parent,false)
                }

            }
        return BaseViewHolder(view)
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if(position == 0){
            holder.layout.findViewById<ImageButton>(R.id.fragment_calender_add_petsBtn).setOnClickListener {
                addClickListener.onClick(it,position)
            }
        }
//        else if(position == petList.size+1){
//            holder.layout.findViewById<ImageButton>(R.id.fragment_calender_add_petsBtn).setOnClickListener {
//                addClickListener.onClick(it,position)
//            }
//        }
        else{
            Glide.with(holder.layout)
                .load("${ApplicationClass.IMGS_URL}${petList[position].photoPath}")
                .into(holder.layout.findViewById(R.id.fragment_calender_pets_item))

            holder.itemView.findViewById<ImageView>(R.id.fragment_calender_pets_item).setOnClickListener {
                itemClickListener.onClick(it,position, petList[position])
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