package com.ssafy.ccd.src.main.information.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.ccd.R
import com.ssafy.ccd.src.dto.YoutubeInfo
import com.ssafy.ccd.src.main.information.InformationActivity
import com.ssafy.ccd.src.main.information.YoutubeDialog

class DogTrainingRecyclerViewAdapter (private val context: Context, private var datas: MutableList<YoutubeInfo>)
    : RecyclerView.Adapter<DogTrainingRecyclerViewAdapter.MsgInfoHolder>() {

    val dialog:YoutubeDialog = YoutubeDialog(context, (context as InformationActivity))
    inner class MsgInfoHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindInfo(data : YoutubeInfo) {
            Glide.with(context).load(data.imageUrl).into(view.findViewById(R.id.itemInfo_iv))
            view.findViewById<TextView>(R.id.itemInfo_tvTitle).text = data.title
            view.findViewById<TextView>(R.id.itemInfo_tvDate).text = data.date
            view.findViewById<TextView>(R.id.itemInfo_tvChannel).text = data.channel
            view.findViewById<ConstraintLayout>(R.id.itemDogTrainingList_cl).setOnClickListener {
                (context as InformationActivity).showDialog(data.imageUrl)
                dialog.callDialog(data.id)
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + data.id + "&ab_channel=%EA%B0%95%ED%98%95%EC%9A%B1%EC%9D%98%EB%B3%B4%EB%93%ACTV-DogTrainerKang"))
//                (context as DogTrainingActivity).startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ) : MsgInfoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_youtube_list, parent, false)
        return MsgInfoHolder(view)
    }


    // ViewHolder에 실제 데이터를 binding 하는 메서드
    override fun onBindViewHolder(holder: MsgInfoHolder, position: Int ) {
        holder.apply {
            bindInfo(datas[position])
        }
    }

    override fun getItemCount(): Int = datas.size

    fun setItem(data : MutableList<YoutubeInfo>) {
        datas = data
        notifyDataSetChanged()
    }
}
