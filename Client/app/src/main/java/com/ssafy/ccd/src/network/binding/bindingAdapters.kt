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
import com.ssafy.ccd.src.main.calender.CalendarDetailAdapter
import com.ssafy.ccd.src.main.calender.CalendarWritePetAdapter
import com.ssafy.ccd.src.main.diary.DiaryAdapter
import com.ssafy.ccd.src.main.diary.DiaryHashAdapter
import com.ssafy.ccd.src.main.diary.DiaryPhotoRvAdapter
import com.ssafy.ccd.src.main.home.BoardAdapter
import com.ssafy.ccd.src.main.home.Community.LocalBoardAdapter
import com.ssafy.ccd.src.main.home.Community.LocalCommentAdapter
import com.ssafy.ccd.src.main.home.Community.SearchAdapter
import com.ssafy.ccd.src.main.home.Community.ShareBoardAdapter
import com.ssafy.ccd.src.main.home.HomeProfilePetsAdapter
import com.ssafy.ccd.src.main.home.NotificationAdapter
import com.ssafy.ccd.src.main.mypage.MyPostRecyclerviewAdapter
import com.ssafy.ccd.src.main.mypage.MyScheduleRecyclerviewAdapter
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
            .load(R.drawable.logo)
            .circleCrop()
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

@BindingAdapter("calendarType")
fun bindCalendarType(textView: TextView, type:Int){
    if(type == 1){
        textView.text = "접종  |"
    }else if(type == 2){
        textView.text = "산책  |"
    }else{
        textView.text = "기타  |"
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

@RequiresApi(Build.VERSION_CODES.N)
@BindingAdapter("unixToDateTime")
fun bindDateFormat(textView: TextView, unixTime: String) {
    textView.text = CommonUtils.unixTimeToDateFormat(unixTime.toLong())
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
@BindingAdapter("schedulerListData")
fun bindScheduleRecyclerView(recyclerView: RecyclerView, data:List<Schedule>?){
    var adapter = recyclerView.adapter as CalendarDetailAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as CalendarDetailAdapter
    }
    adapter.list = data as MutableList<Schedule>
    adapter.notifyDataSetChanged()
}
@BindingAdapter("schedulerTodayListData")
fun bindScheduleTodayRecyclerView(recyclerView: RecyclerView, data:List<Schedule>?){
    var adapter = recyclerView.adapter as MyScheduleRecyclerviewAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as MyScheduleRecyclerviewAdapter
    }
    adapter.list = data as MutableList<Schedule>
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
}

@BindingAdapter("localPostListData")
fun bindPostListRecyclerView(recyclerView: RecyclerView, data: MutableList<Board>) {
    var adapter = recyclerView.adapter as LocalBoardAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as LocalBoardAdapter
    }
    adapter.postList = data
//    adapter.notifyDataSetChanged()

//    val adapter = recyclerView.adapter as LocalBoardAdapter
//    adapter.submitList(data)
//    adapter.notifyDataSetChanged()
}

@BindingAdapter("localCommentListData")
fun bindLocalCommentList(recyclerView: RecyclerView, data: MutableList<Comment>) {
    var adapter = recyclerView.adapter as LocalCommentAdapter
    if (recyclerView.adapter == null) {
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    } else {
        adapter = recyclerView.adapter as LocalCommentAdapter
    }
    adapter.commentList = data
}

@BindingAdapter("sharePostListData")
fun bindSharePostListRecyclerView(recyclerView: RecyclerView, data: MutableList<Board>) {
    var adapter = recyclerView.adapter as ShareBoardAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as ShareBoardAdapter
    }
    adapter.postList = data
}

@BindingAdapter("notificationList")
fun bindNotificationList(recyclerView: RecyclerView, data:List<Notification>?){
    var adapter = recyclerView.adapter as NotificationAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as NotificationAdapter
    }
    adapter.list = data as MutableList<Notification>
    adapter.notifyDataSetChanged()
}
@BindingAdapter("boardAllListbyUser")
fun bindBoardAllListbyUser(recyclerView: RecyclerView, data:MutableList<Board>){
    var adapter = recyclerView.adapter as MyPostRecyclerviewAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as MyPostRecyclerviewAdapter
    }
    adapter.list = data
    adapter.notifyDataSetChanged()
}
@BindingAdapter("searchBoardList")
fun bindSearchBoardList(recyclerView: RecyclerView, data:List<Board>?){
    var adapter = recyclerView.adapter as SearchAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as SearchAdapter
    }
    adapter.list = data as MutableList<Board>
    adapter.notifyDataSetChanged()
}
@BindingAdapter("boardType")
fun bindConvertBoardType(textView: TextView,type:Int){
    if(type == 1){
        textView.text = "울동네"
    }else if(type == 2){
        textView.text = "궁금해"
    }else if(type == 3){
        textView.text = "공유해"
    }
}
@BindingAdapter("notificationType")
fun bindConvertNotificationType(imageView: ImageView,type:Int){
    if(type == 1){
        //공지사항
        imageView.setImageResource(R.drawable.notinotice)
    }else if(type == 2){
        //이벤트
        imageView.setImageResource(R.drawable.notievent)
    }else if(type == 3){
        //user
        imageView.setImageResource(R.drawable.notiuser)
    }
}
@BindingAdapter("imageUriSize")
fun bindConvertUriSize(textView: TextView,size:Int){
    textView.text = "${size}/10"
}
//@BindingAdapter("localReplyListData")
//fun bindLocalReplyList(recyclerView: RecyclerView, data: MutableList<Comment>) {
//    var adapter = recyclerView.adapter as LocalCommentReplyAdapter
//    if (recyclerView.adapter == null) {
//        adapter.setHasStableIds(true)
//        recyclerView.adapter = adapter
//    } else {
//        adapter = recyclerView.adapter as LocalCommentReplyAdapter
//    }
//    adapter.commentList = data
//}