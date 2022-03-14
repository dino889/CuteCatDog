package com.ssafy.ccd.src.network.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.mypage.PetListRecyclerviewAdapter

@BindingAdapter("imageUrlPet")
fun bindImagePets(imgView: ImageView, imgUrl:String?){
    Glide.with(imgView.context)
        .load("${ApplicationClass.IMGS_URL}${imgUrl}")
        .into(imgView)
}

@BindingAdapter("petListData")
fun bindPetRecyclerView(recyclerView: RecyclerView, data:MutableList<Pet>?){
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