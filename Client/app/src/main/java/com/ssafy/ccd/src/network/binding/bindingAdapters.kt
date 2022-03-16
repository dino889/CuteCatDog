package com.ssafy.ccd.src.network.binding

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.home.HomeProfilePetsAdapter
import com.ssafy.ccd.src.main.mypage.PetListRecyclerviewAdapter
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("myPagePetInfo")
fun bindPetConvertAgeandGender(textView: TextView, data:Pet?){
    var result = ""
    var now = LocalDate.now()

//    var parseBirth = LocalDate.parse(data?.birth, DateTimeFormatter.ISO_DATE)
    var birthYear = data?.birth.toString().substring(0,4)
    var curyearInt = Integer.parseInt(now.year.toString())
    var birthyearInt = Integer.parseInt(birthYear.toString())

    var age = curyearInt - birthyearInt + 1

    result += "${age}세 "
    if(data?.gender == 0){
        result += "남아"
    }else{
        result += "여아"
    }
    textView.text = result
}

@BindingAdapter("myPageInfoNeut")
fun bindPetConvertNeutering(textView:TextView, neuter:Int){
    if(neuter == 0){
        textView.text = "O"
    }else{
        textView.text = "X"
    }
}

@BindingAdapter("homePetListData")
fun bindHomePetRecyclerView(recyclerView: RecyclerView, data:List<Pet>?){
    var adapter = recyclerView.adapter as HomeProfilePetsAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as HomeProfilePetsAdapter
    }

    adapter.list = data as MutableList<Pet>
    adapter.notifyDataSetChanged()
}