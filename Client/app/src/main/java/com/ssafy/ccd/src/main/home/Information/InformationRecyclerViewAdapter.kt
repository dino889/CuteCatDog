package com.ssafy.ccd.src.main.home.Information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.src.dto.ItemInfo
import com.ssafy.ccd.src.main.MainActivity


class InformationRecyclerViewAdapter(private var context: Context, private var datas: MutableList<ItemInfo>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class InformationHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindInfo(data: ItemInfo) {
            val imageResource = context.resources.getIdentifier(data.img, "drawable", context.packageName)

//            view.findViewById<TextView>(R.id.itemInformation_tvTitle).text = data.title
            view.findViewById<ImageView>(R.id.itemInformation_iv).setImageResource(imageResource)
            view.findViewById<CardView>(R.id.itemInformation_cv).setOnClickListener {
                (context as MainActivity).startActivity(data.intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ) : RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_information, parent, false)
        return InformationHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int ) {
        if(holder is InformationHolder) {
            holder.bindInfo(datas[position])
        }
    }

    override fun getItemCount(): Int = datas.size
}
