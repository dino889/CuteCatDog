package com.ssafy.ccd.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.ccd.R

class HomeProfilePetsAdapter(var list:ArrayList<String>):RecyclerView.Adapter<HomeProfilePetsAdapter.HomePetsViewHolder>() {
    val ITEM = 1
    val FOOTER = 2

    class HomePetsViewHolder(val layout: View):RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePetsViewHolder {
        var view =
            when(viewType){
                FOOTER ->
                    LayoutInflater.from(parent.context).inflate(R.layout.item_home_pets_footer,parent,false)
                else ->
                    LayoutInflater.from(parent.context).inflate(R.layout.item_home_pets_list,parent,false)
            }
        return HomePetsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePetsViewHolder, position: Int) {
        if(position == list.size+1){
            var footer = holder.layout.findViewById<ConstraintLayout>(R.id.fragment_home_rvItemPetsFooter)
            footer.setOnClickListener {
                list.add("")
                this.notifyDataSetChanged()
            }
        }else{
            var petImg = holder.layout.findViewById<ImageView>(R.id.fragment_home_ivPetsImg)
        }
    }

    override fun getItemCount(): Int {
        return list.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == list.size+1){
            FOOTER
        }else{
            ITEM
        }
    }
}