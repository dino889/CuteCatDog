package com.ssafy.ccd.src.main.diary

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.src.dto.Photo
import java.lang.Exception

class DiaryPhotoUpdateAdapter(): RecyclerView.Adapter<DiaryPhotoUpdateAdapter.PhotoViewHolder>(){
    var list = mutableListOf<Photo>()
    inner class PhotoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(data: Photo){
            var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
            var storageRef = storage.reference
            storageRef.child("${data.photo}").downloadUrl.addOnSuccessListener(object :
                OnSuccessListener<Uri> {
                override fun onSuccess(p0: Uri?) {
                    Glide.with(itemView)
                        .load(p0)
                        .into(itemView.findViewById(R.id.diaryFragment_iv_imgPath))
                }

            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: Exception) {
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_photo,parent,false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}