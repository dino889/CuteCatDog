package com.ssafy.ccd.src.main.home


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.databinding.ItemBannerListBinding

class BannerAdapter(var list: MutableList<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(private val binding: ItemBannerListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(res: Int) {
            binding.eventIv.setImageResource(res)
            binding.eventIv.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        var listItemBinding = ItemBannerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.onBind(list[position % list.size])
    }

    override fun getItemCount(): Int = list.size
}