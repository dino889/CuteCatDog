package com.ssafy.ccd.src.main.calender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R

class CalenderPetsAdapter(var list:ArrayList<String>) : RecyclerView.Adapter<CalenderPetsAdapter.PetsViewHolder>(){
    val ITEM = 1
    val FOOTER = 2

    class PetsViewHolder(val layout: View):RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        var view =
            when(viewType){
                FOOTER ->
                    LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets_footer,parent,false)
                else ->
                    LayoutInflater.from(parent.context).inflate(R.layout.item_calender_pets,parent,false)
            }
        return PetsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        if(position == list.size+1){
            var footer = holder.layout.findViewById<ImageButton>(R.id.fragment_calender_add_petsBtn)
            footer.setOnClickListener {
                list.add("")
                this.notifyDataSetChanged()
            }
        }else{
            var petImg = holder.layout.findViewById<ImageView>(R.id.fragment_calender_pets_item)
        }
    }

    override fun getItemCount(): Int {
        return list.size+2
    }
    override fun getItemViewType(position: Int): Int {
        return if(position == list.size+1){
            FOOTER
        }else{
            ITEM
        }
    }
}