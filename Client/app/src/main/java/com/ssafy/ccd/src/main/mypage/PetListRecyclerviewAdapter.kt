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

    open class BaseViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    class FooterViewHolder(itemView:View) : BaseViewHolder(itemView)
    class ItemViewHolder(itemView: View): BaseViewHolder(itemView){
        fun bind(data:Pet){
            Glide.with(itemView)
                .load("${ApplicationClass.IMGS_URL}${data.photoPath}")
                .into(itemView.findViewById(R.id.fragment_calender_pets_item))
        }
    }

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
            else -> throw Exception("Unknow viewType ${viewType}")
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            holder.bind(petList[position])
            holder.itemView.findViewById<ImageView>(R.id.fragment_calender_pets_item).setOnClickListener {
                itemClickListener.onClick(it,position, petList[position])
            }
        }
        if(holder is FooterViewHolder){
            holder.itemView.findViewById<ImageButton>(R.id.fragment_calender_add_petsBtn).setOnClickListener {
                addClickListener.onClick(it,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return petList.size+1
    }

    override fun getItemViewType(position: Int): Int =
        when(position){
            petList.size+1 -> FOOTER
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