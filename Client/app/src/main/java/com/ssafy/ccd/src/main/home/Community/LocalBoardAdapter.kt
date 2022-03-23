package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.main.home.BoardAdapter

class LocalBoardAdapter (var postList : MutableList<Board>, val context: Context) : RecyclerView.Adapter<BoardAdapter.BoardBaseHolder>(){

    inner class LocalBoardViewHolder(private val binding: Item
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardAdapter.BoardBaseHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BoardAdapter.BoardBaseHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}