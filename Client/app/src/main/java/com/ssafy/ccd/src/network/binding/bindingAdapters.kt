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
import com.ssafy.ccd.src.dto.*
import com.ssafy.ccd.src.dto.Diary
import com.ssafy.ccd.src.dto.Hashtag
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.dto.Photo
import com.ssafy.ccd.src.main.calender.CalendarWritePetAdapter
import com.ssafy.ccd.src.main.diary.DiaryAdapter
import com.ssafy.ccd.src.main.diary.DiaryHashAdapter
import com.ssafy.ccd.src.main.diary.DiaryPhotoRvAdapter
import com.ssafy.ccd.src.main.home.BoardAdapter
import com.ssafy.ccd.src.main.home.HomeProfilePetsAdapter
import com.ssafy.ccd.src.main.mypage.PetListRecyclerviewAdapter
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import com.ssafy.ccd.util.CommonUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@BindingAdapter("imageUrlCalendarPets")
fun bindImageCalendarPets(imgView: ImageView,imgUrl: String?){
    if(imgUrl == null || imgUrl == ""){
        Glide.with(imgView.context)
            .load(R.drawable.logo)
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

        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(p0: Exception) {
            }
        })
    }
}
@BindingAdapter("imageUrlDiary")
fun bindImageDiary(imgView: ImageView, imgUrl: String?){
    if(imgUrl == null || imgUrl == ""){
        Glide.with(imgView.context)
            .load(R.drawable.defaultimg)
            .into(imgView)
    }else{
        val storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
        val storageRef = storage.reference
        storageRef.child("$imgUrl").downloadUrl.addOnSuccessListener { p0 ->
            Glide.with(imgView.context)
                .load(p0)
                .into(imgView)
        }.addOnFailureListener { }
    }

}
@BindingAdapter("imageUrlDiaryWrite")
fun bindImageDiaryWrite(imgView: ImageView, imgUrl: String?) {
    var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
    var storageRef = storage.reference
    storageRef.child("$imgUrl").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
        override fun onSuccess(p0: Uri?) {
            Glide.with(imgView.context)
                .load(p0)
                .into(imgView)
        }

    }).addOnFailureListener(object : OnFailureListener {
        override fun onFailure(p0: Exception) {
        }
    })
}
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
}

@BindingAdapter("imageUrlUser")
fun bindImageUser(imgView: ImageView, imgUrl: String?) {
    if(imgUrl == null || imgUrl == ""){
        Glide.with(imgView.context)
            .load(R.drawable.defaultimg)
            .into(imgView)

    } else if(imgUrl.contains("https://")) {
        Glide.with(imgView.context)
            .load(imgUrl)
            .circleCrop()
            .into(imgView)

    } else {
        val storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
        val storageRef = storage.reference
        storageRef.child("$imgUrl").downloadUrl.addOnSuccessListener { p0 ->
            Glide.with(imgView.context)
                .load(p0)
                                    .circleCrop()
                .into(imgView)
        }.addOnFailureListener(object : OnFailureListener {
            override fun onFailure(p0: Exception) {
            }
        })
    }
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


@BindingAdapter("petListCalendarData")
fun bindPetCalendarRecyclerView(recyclerView: RecyclerView, data:List<Pet>?){
    var adapter = recyclerView.adapter as CalendarWritePetAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as CalendarWritePetAdapter
    }

    adapter.list = data as MutableList<Pet>
    adapter.notifyDataSetChanged()
}

@BindingAdapter("myPageConvertBirth")
fun bindPetConvertBirth(textView: TextView,data:String?){
    if( data == null || data == ""){
        textView.text = ""
    }else{
        textView.text = CommonUtils.makeBirthString(data)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("myPagePetInfo")
fun bindPetConvertAgeandGender(textView: TextView, data:Pet?){
    var result = ""
    if(data != null){
        var now = LocalDate.now()

//    var parseBirth = LocalDate.parse(data?.birth, DateTimeFormatter.ISO_DATE)
        var birthYear = CommonUtils.makeBirthString(data?.birth).substring(0,4)
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
    }else{
        textView.text = ""
    }
}

@BindingAdapter("myPageInfoNeut")
fun bindPetConvertNeutering(textView:TextView, neuter:Int){
    if(neuter == 0){
        textView.text = "X"
    }else{
        textView.text = "O"
    }
}

@BindingAdapter("diaryDate")
fun bindDiaryConvertDate(textView:TextView, date:String){
    var diary = CommonUtils.makeBirthString(date)
    var monthTmp = diary.substring(6,8)
    var dayTmp = diary.substring(9,12)
    var months = CommonUtils.convertEnglishMonth(monthTmp.toInt())
    var month = months.substring(0,3)
    textView.text = "${dayTmp} \n ${month}"
}

@BindingAdapter("userNick")
fun bindUserNick(textView: TextView, userNick: String) {
    textView.text = userNick +  "님"
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

@BindingAdapter("diaryPhotoListData")
fun bindDiaryPhotoRecyclerView(recyclerView: RecyclerView, data:List<Photo>?){
    var adapter = recyclerView.adapter as DiaryPhotoRvAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as DiaryPhotoRvAdapter
    }
    adapter.list = data as MutableList<Photo>
    adapter.notifyDataSetChanged()
}

@BindingAdapter("diaryListData")
fun bindDiaryRecyclerView(recyclerView: RecyclerView, data:List<Diary>?){
    var adapter = recyclerView.adapter as DiaryAdapter
    if(recyclerView.adapter ==null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as DiaryAdapter
    }
    adapter.list = data as MutableList<Diary>
    adapter.notifyDataSetChanged()

}

@BindingAdapter("diaryHashListData")
fun bindDiaryHashRecyclerView(recyclerView: RecyclerView, data:List<Hashtag>?){
    var adapter = recyclerView.adapter as DiaryHashAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as DiaryHashAdapter
    }
    adapter.list = data as MutableList<Hashtag>
    adapter.notifyDataSetChanged()
}

/**
 * board 관련 bindingAdapter
 */
@BindingAdapter("homePostList") // BoardFragment + BoardAdapter
fun bindBoardRecyclerView(recyclerView: RecyclerView, data: List<Board>?) {

    var adapter = recyclerView.adapter as BoardAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as BoardAdapter
    }

    val tmp = mutableListOf<Board>()
    if(data != null) {
        if (data.size >= 5) {
            for (i in 0 until 5) {
                tmp.add(data[i])
            }
            adapter.postList = tmp as MutableList<Board>
        } else {
            adapter.postList = data as MutableList<Board>
        }
    }
    adapter.notifyDataSetChanged()
}