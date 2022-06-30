package com.ssafy.ccd.src.main.home.Community

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemSearchBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User

class SearchAdapter(var list: List<Board>) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(),Filterable {

    lateinit var userList:MutableList<User>
    var filteredList = list
    inner class SearchViewHolder(private val binding:ItemSearchBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: Board){
            Log.d("Filter", "${data}")
            binding.board = data
            for (user in userList) {    // 작성자 nickname, profileImg 세팅
                if(data.userId == user.id) {
                    binding.user = user
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_search,parent,false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.apply {
            Log.d("Filter", "${filteredList[position]}")
            bind(filteredList[position])
            itemView.setOnClickListener {
                itemClickListener.onClick(it, position,filteredList[position].typeId, filteredList[position].id, filteredList[position].userId)

            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getItemId(position: Int): Long {
        return filteredList.get(position).id.toLong()
    }
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                filteredList = if(charString.isEmpty()){
                    list
                }else{
                    val filteringList = ArrayList<Board>()
                    for(item in list!!){
                        if(item!!.content.contains(charString)) filteringList.add(item)
                        if(item!!.title.contains(charString)) filteringList.add(item)
                    }
                    filteringList
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<Board>
                notifyDataSetChanged()
            }

        }
    }
    interface ItemClickListener {
        fun onClick(view: View, position: Int, typeId: Int, boardId:Int, userId:Int)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setOnItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}