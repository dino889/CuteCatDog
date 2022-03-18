package com.ssafy.ccd.src.main.diary

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.DataBinderMapperImpl
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemDiaryPhotoBinding
import com.ssafy.ccd.src.dto.Photo
import java.lang.Exception

class DiaryPhotoAdapter(): RecyclerView.Adapter<DiaryPhotoAdapter.PhotoViewHolder>() {
    var photoList = arrayListOf<Uri>()
    inner class PhotoViewHolder(private val binding:ItemDiaryPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Uri){
//            var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
//            var storageRef = storage.reference
//            storageRef.child("$data").downloadUrl.addOnSuccessListener(object :
//                OnSuccessListener<Uri> {
//                override fun onSuccess(p0: Uri?) {
//                    Glide.with(itemView)
//                        .load(p0)
//                        .into(itemView.findViewById(R.id.diaryFragment_iv_imgPath))
//                }
//
//            }).addOnFailureListener(object : OnFailureListener {
//                override fun onFailure(p0: Exception) {
//                }
//            })
            binding.diaryFragmentIvImgPath.setImageURI(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_diary_photo,parent,false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photos = photoList[position]
        holder.apply {
            bind(photos)
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}