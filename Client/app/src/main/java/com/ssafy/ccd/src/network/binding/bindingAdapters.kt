package com.ssafy.ccd.src.network.binding

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.mypage.PetListRecyclerviewAdapter
import java.lang.Exception

@BindingAdapter("imageUrlPet")
fun bindImagePets(imgView: ImageView, imgUrl:String?){
    if(imgUrl == null || imgUrl == ""){
        Glide.with(imgView.context)
            .load(R.drawable.logo)
            .circleCrop()
            .into(imgView)
    }else{
        var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
        var storageRef = storage.reference
        storageRef.child("$imgUrl").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
            override fun onSuccess(p0: Uri?) {
                Glide.with(imgView.context)
                    .load(p0)
                    .circleCrop()
                    .into(imgView)
            }

        }).addOnFailureListener(object: OnFailureListener {
            override fun onFailure(p0: Exception) {
            }
        })
    }
//    Glide.with(imgView.context)
//        .load("${ApplicationClass.IMGS_URL}${imgUrl}")
//        .into(imgView)
}

@BindingAdapter("petListData")
fun bindPetRecyclerView(recyclerView: RecyclerView, data:List<Pet>?){
    var adapter = recyclerView.adapter as PetListRecyclerviewAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as PetListRecyclerviewAdapter
    }

    adapter.petList = data as MutableList<Pet>
    adapter.notifyDataSetChanged()
}