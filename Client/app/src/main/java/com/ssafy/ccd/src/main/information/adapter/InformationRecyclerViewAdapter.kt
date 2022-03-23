package com.ssafy.ccd.src.main.information.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemLoadingBinding
import com.ssafy.ccd.src.dto.YoutubeInfo
import com.ssafy.ccd.src.main.information.InformationActivity
import com.ssafy.ccd.src.main.information.YoutubeDialog
import com.ssafy.ccd.src.main.information.util.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

private const val VIEW_TYPE_ITEM = 0
private const val VIEW_TYPE_LOADING = 1

class InformationRecyclerViewAdapter (private val context: Context, private var datas: MutableList<YoutubeInfo>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dialog: YoutubeDialog = YoutubeDialog(context, (context as InformationActivity))
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
    val dateUtil = DateUtils()

    // 아이템이 들어가는 경우
    inner class InformationHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindInfo(data: YoutubeInfo) {
            Glide.with(context).load(data.imageUrl).into(view.findViewById(R.id.itemInfo_iv))
            view.findViewById<TextView>(R.id.itemInfo_tvTitle).text = data.title
            view.findViewById<TextView>(R.id.itemInfo_tvDate).text = format.format(dateUtil.parse(data.date)).toString()
            view.findViewById<TextView>(R.id.itemInfo_tvChannel).text = data.channel
            view.findViewById<ConstraintLayout>(R.id.itemDogTrainingList_cl).setOnClickListener {
                dialog.callDialog(data.id)
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + data.id + "&ab_channel=%EA%B0%95%ED%98%95%EC%9A%B1%EC%9D%98%EB%B3%B4%EB%93%ACTV-DogTrainerKang"))
//                (context as DogTrainingActivity).startActivity(intent)
            }
        }
    }

    // 아이템뷰에 프로그레스바가 들어가는 경우
    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ) : RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_youtube_list, parent, false)
                InformationHolder(view)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int ) {
        if(holder is InformationHolder){
            holder.bindInfo(datas[position])
        }else{

        }
    }

    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (datas[position].type) {
            0 -> VIEW_TYPE_ITEM
            else -> VIEW_TYPE_LOADING
        }
    }

    override fun getItemCount(): Int = datas.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(data : MutableList<YoutubeInfo>) {
        if(datas.size > 0 ) datas.removeAt(datas.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
        datas.addAll(data)
        datas.add(YoutubeInfo("", "", "", "", "", 1))
        notifyDataSetChanged()
    }

    fun deleteLoading(){
        if(datas.size == 0) return
        datas.removeAt(datas.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
        notifyDataSetChanged()
    }
}
