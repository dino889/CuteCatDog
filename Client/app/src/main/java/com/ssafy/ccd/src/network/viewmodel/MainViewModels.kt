package com.ssafy.ccd.src.network.viewmodel

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.type.LatLng
import com.ssafy.ccd.src.dto.*
import com.ssafy.ccd.src.dto.Calendar
import com.ssafy.ccd.src.network.service.*
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainViewModels_ccd"
class MainViewModels : ViewModel() {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * @author Jiwoo
     * USER ViewModel
     */
    private val _allUserList = MutableLiveData<MutableList<User>>()
    private val _loginUserInfo = MutableLiveData<User>()
    private val _userInfo = MutableLiveData<User>()
    var userId = 0
    private var _userLoc : Location? = null
    private var _userAddr : String? = null

    val allUserList :  LiveData<MutableList<User>>
        get() = _allUserList

    val loginUserInfo : LiveData<User>
        get() = _loginUserInfo

    val userInformation : LiveData<User>
        get() = _userInfo

    val userLoc : Location?
        get() = _userLoc

    val userAddr : String?
        get() = _userAddr

    private fun setLoginUserInfo(user: User) = viewModelScope.launch {
        _loginUserInfo.value = user
    }

    private fun setUserInfo(user: User) = viewModelScope.launch {
        _userInfo.value = user
    }

    private fun setAllUserList(userList : MutableList<User>) = viewModelScope.launch {
        _allUserList.value = userList
    }

    fun setUserLoc(location : Location, addr: String) {
        _userLoc = location
        _userAddr = addr
    }


    suspend fun getAllUserList() {
        val response = UserService().selectAllUsers()

        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.success) {
                        if(res.data["user"] != null && res.message == "회원 정보 조회 성공") {
                            val type = object : TypeToken<MutableList<User>>() {}.type
                            val userList: MutableList<User> = CommonUtils.parseDto(res.data["user"]!!, type)
                            setAllUserList(userList)
                        } else {
                            Log.e(TAG, "getAllUserList: ${res.message}", )  // 회원 정보 조회 실패
                        }
                    } else {
                        Log.e(TAG, "getAllUserList: 서버 통신 실패 ${res.message}", )
                    }
                }
            }
        }
    }

    suspend fun getUserInfo(userId: Int, loginChk : Boolean) : Int {
        var returnRes = -1
        val response = UserService().readUserInfo(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res.success == true) {
                        if(res.data["user"] != null && res.message == "회원 정보 조회 성공") {
                            val type: Type = object : TypeToken<User>() {}.type
                            val user = CommonUtils.parseDto<User>(res.data["user"]!!, type)
                            if(loginChk == true) {  // login user
                                setLoginUserInfo(user)
                            } else {
                                setUserInfo(user)
                            }
                            returnRes = 1
                        } else if(res.data["user"] == null) {
                            returnRes = 2   // 탈퇴한 회원 정보 조회 또는 에러
                        }
                    } else {
                        returnRes = -1  // 서버 통신 오류
                        Log.e(TAG, "getUserInfo: ${res.message}")
                    }

                } else {
                    Log.d(TAG, "getUserInfoError: ${response.message()}")
                }
            }
        }
        return returnRes
    }

    suspend fun join(user: User) : Message {
        var result = Message()
        val response = UserService().createUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    suspend fun login(user: User) : Message {
        var result = Message()
        val response = UserService().loginUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    suspend fun existsChkUserEmail(email: String) : Message {
        var result = Message()
        val response = UserService().existsUserEmail(email)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    suspend fun sendCodeToEmail(email: String) : Message {
        var result = Message()
        val response = UserService().verifyUserEmail(email)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * Board ViewModel
     * @author Jiwoo
     * @since 03.22.22.
     */
    private val _postAllList = MutableLiveData<MutableList<Board>>()
    private val _postListByType = MutableLiveData<MutableList<Board>>()
    private val _locPostList = MutableLiveData<MutableList<Board>>()
    private val _qnaPostList = MutableLiveData<MutableList<Board>>()
    private val _sharePostList = MutableLiveData<MutableList<Board>>()
    private val _boardAllByUser = MutableLiveData<MutableList<Board>>()
    private val _likePostsByUserId = MutableLiveData<MutableList<Int>>()
    private val _postDetail = MutableLiveData<Board>()
    private val _commentAllList = MutableLiveData<MutableList<Comment>>()
    private val _commentListWoParents = MutableLiveData<MutableList<Comment>>()
    var boardId = 0

    val postAllList : LiveData<MutableList<Board>>
        get() = _postAllList

    val postListByType : LiveData<MutableList<Board>>
        get() = _postListByType

    val locPostList : LiveData<MutableList<Board>>
        get() = _locPostList

    val qnaPostList : LiveData<MutableList<Board>>
        get() = _qnaPostList

    val sharePostList : LiveData<MutableList<Board>>
        get() = _sharePostList

    val likePostsByUserId : LiveData<MutableList<Int>>
        get() = _likePostsByUserId

    val postDetail : LiveData<Board>
        get() = _postDetail

    val commentAllList : LiveData<MutableList<Comment>>
        get() = _commentAllList

    val commentListWoParents : LiveData<MutableList<Comment>>
        get() = _commentListWoParents

    val boardListByUser : LiveData<MutableList<Board>>
        get() = _boardAllByUser

    private fun setAllPostList(postList : MutableList<Board>) = viewModelScope.launch {
        _postAllList.value = postList
    }

    private fun setPostListByType(postList: MutableList<Board>) = viewModelScope.launch {
        _postListByType.value = postList
    }

    private fun setLocPostList(postList: MutableList<Board>) = viewModelScope.launch {
        _locPostList.value = postList
    }

    private fun setQnaPostList(postList: MutableList<Board>) = viewModelScope.launch {
        _qnaPostList.value = postList
    }

    private fun setSharePostList(postList: MutableList<Board>) = viewModelScope.launch {
        _sharePostList.value = postList
    }

    private fun setLikePosts(postIdList : MutableList<Int>) = viewModelScope.launch {
        _likePostsByUserId.value = postIdList
    }

    private fun setPostDetail(post : Board) = viewModelScope.launch {
        _postDetail.value = post
    }

    private fun setAllCommentList(commentList : MutableList<Comment>) = viewModelScope.launch {
        _commentAllList.value = commentList
    }

    private fun setCommentListWoParents(commentList: MutableList<Comment>) = viewModelScope.launch {
        _commentListWoParents.value = commentList
    }

    private fun setBoardListByUser(board:MutableList<Board>) = viewModelScope.launch {
        _boardAllByUser.value = board
    }

    suspend fun getAllPostList() {
        val response = BoardService().selectAllPostList()

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res.data["boards"] != null && res.success == true) {
                        val type = object : TypeToken<MutableList<Board>>() {}.type
                        val postList: MutableList<Board> = CommonUtils.parseDto(res.data["boards"]!!, type)

                        val locBoard : MutableList<Board> = mutableListOf()
                        val qnaBoard : MutableList<Board> = mutableListOf()
                        val shareBoard : MutableList<Board> = mutableListOf()
                        for (i in postList) {
                            when(i.typeId) {
                                1 -> {
                                    locBoard.add(i)
                                }
                                2 -> {
                                    qnaBoard.add(i)
                                }
                                3 -> {
                                    shareBoard.add(i)
                                }
                            }
                        }

                        setAllPostList(postList)
                        setLocPostList(locBoard)
                        setQnaPostList(qnaBoard)
                        setSharePostList(shareBoard)

                    } else {
                        Log.e(TAG, "getAllPostList: ${res.message}", )
                    }
                }
            }
        }
    }

    suspend fun getPostListByType(typeId: Int) {
        val response = BoardService().selectPostListByType(typeId)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res.data["boards"] != null && res.success) {
                        val type = object : TypeToken<MutableList<Board>>() {}.type
                        val postList: MutableList<Board> = CommonUtils.parseDto(res.data["boards"]!!, type)
                        if (typeId == 1) {
//                            setLocPostList(postList)
                        } else if (typeId == 2) {
                            setQnaPostList(postList)
                        } else if (typeId == 3) {
                            setSharePostList(postList)
                        }
                        setPostListByType(postList)
                    } else {
                        Log.e(TAG, "getPostListByType: ${res.message}", )
                    }
                }
            }
        }
    }

    suspend fun getLikePostsByUserId(userId: Int) {
        val response = BoardService().selectLikePostsByUserId(userId)

        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if (res.data["board"] != null && res.success) {
                        setLikePosts(res.data["board"] as MutableList<Int>)
                    } else {
                        Log.e(TAG, "getLikePostsByUserId: ${res.message}", )
                    }
                }
            }
        }
    }

    suspend fun getPostDetail(id: Int) {
        val response = BoardService().selectPostDetail(id)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if (res.data["board"] != null && res.success) {
                        val type = object : TypeToken<Board>() {}.type
                        val post: Board = CommonUtils.parseDto(res.data["board"]!!, type)
                        setPostDetail(post)
                        setAllCommentList(post.commentList as MutableList<Comment>)
                    } else {
                        Log.e(TAG, "getPostDetail: ${res.message}", )
                    }
                }
            }
        }
    }

    suspend fun getCommentList(postId : Int) {
        val response = BoardService().selectCommentList(postId)

        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.data["comments"] != null && res.success == true) {
                        val type = object : TypeToken<MutableList<Comment>>() {}.type
                        val commentList: MutableList<Comment> = CommonUtils.parseDto(res.data["comments"]!!, type)
                        setAllCommentList(commentList)

                        val tmp = mutableListOf<Comment>()
                        for(cmt in commentList) {
                            Log.d(TAG, "LocalCommentAdapter_ccd: ${cmt.parent}")
                            if(cmt.parent == null || cmt.parent.toString() == "null") {
                                tmp.add(cmt)
                            }
                        }
                        setCommentListWoParents(tmp)
                    }
                }
            }
        }
    }

    suspend fun getBoardListByUser(userId:Int){
        val response = BoardService().selectBoardByUserId(userId)
        viewModelScope.launch {
            if(response.code() == 200){
                val res = response.body()
                if(res!=null){
                    if(res.success){
                        val type = object : TypeToken<MutableList<Board?>?>() {}.type
                        val boards = CommonUtils.parseDto<MutableList<Board>>(res.data.get("boards")!!,type)
                        setBoardListByUser(boards)
                    }
                }
            }
        }
    }

    suspend fun getLocPostListByUserLoc(userLoc: Location) {
        val response = BoardService().selectLocalPostList(userLoc.latitude, userLoc.longitude)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.data["boards"] != null && res.success == true) {
                        val type = object : TypeToken<MutableList<Board>>() {}.type
                        val postList: MutableList<Board> = CommonUtils.parseDto(res.data["boards"]!!, type)
                        setLocPostList(postList)

                    }
                }
            }
        }
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * PET VIEW MODEL
     * */
    private val _petsList = MutableLiveData<MutableList<Pet>>()
    private val _myPetsList = MutableLiveData<MutableList<Pet>>()
    private val _pet = MutableLiveData<Pet>()
    private val _kinds = MutableLiveData<MutableList<PetKind>>()
    private val _kind = MutableLiveData<PetKind>()
    var petId:Int = -1

    val petsList : LiveData<MutableList<Pet>>
        get() = _petsList
    val myPetsList : LiveData<MutableList<Pet>>
        get() = _myPetsList
    val pet : LiveData<Pet>
        get() = _pet
    val kinds : LiveData<MutableList<PetKind>>
        get() = _kinds
    val kind : LiveData<PetKind>
        get() = _kind

    fun setPetList(list:MutableList<Pet>) = viewModelScope.launch {
        _petsList.value = list
    }
    fun setMyPetList(list:MutableList<Pet>) = viewModelScope.launch {
        _myPetsList.value = list
    }
    fun setPet(pet:Pet) = viewModelScope.launch {
        _pet.value = pet
    }
    fun setKinds(list:MutableList<PetKind>) = viewModelScope.launch {
        _kinds.value = list
    }
    fun setKind(kind:PetKind) = viewModelScope.launch {
        _kind.value = kind
    }

    suspend fun getPetsAllList(){
        val response = PetService().petsAllListService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res != null){
                    if(res.success){
                        Log.d(TAG, "getPetsAllList: ${res.data}")
                        var type = object : TypeToken<MutableList<Pet?>?>() {}.type
                        var pet:MutableList<Pet> = CommonUtils.parseDto(res.data.get("pets")!!,type)
                        setPetList(pet)
                    }else{
                        Log.d(TAG, "getPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getPetsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getMyPetsAllList(userId:Int){
        val response = PetService().myPetsAllListService(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    Log.d(TAG, "getMyPetsAllList: ${res}")
                    if(res.success){
                        if(res.data.get("pets")!=null){
                            var type = object : TypeToken<MutableList<Pet?>?>() {}.type
                            var pet:MutableList<Pet> = CommonUtils.parseDto<MutableList<Pet>>(res.data.get("pets")!!, type)
                            setMyPetList(pet)
                            Log.d(TAG, "getMyPetsAllList: ${pet}")
                            Log.d(TAG, "getMyPetsAllList: ${pet.size}")
                        }
                    }else{
                        Log.d(TAG, "getMyPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getMyPetsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getPetDetailList(petId:Int){
        val response = PetService().petsDetailListService(petId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getPetDetailList: ${res.data}")
                        var type = object:TypeToken<Pet>() {}.type
                        var pet = CommonUtils.parseDto<Pet>(res.data.get("pet")!!,type)
                        setPet(pet)
                    }else{
                        Log.d(TAG, "getMyPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getMyPetsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getPetKindsAllList(){
        val response = PetService().kindsAllListService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object :TypeToken<MutableList<PetKind>?>() {}.type
                        var kinds = CommonUtils.parseDto<MutableList<PetKind>>(res.data.get("kinds")!!,type)
                        setKinds(kinds)
                    }else{
                        Log.d(TAG, "getPetKindsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getPetKindsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getKindbyId(kindId:Int){
        val response = PetService().kindsById(kindId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object:TypeToken<PetKind?>() {}.type
                        var kind = CommonUtils.parseDto<PetKind>(res.data.get("kinds")!!,type)
                        setKind(kind)
                    }else{
                        Log.d(TAG, "getKindbyId: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getKindbyId: ${response.message()}")
                }
            }
        }
    }
    /**
     * Diary View Model
     * @author BOYEON
     * @Date 2022-03-18
     * */

    val _photoUriList = arrayListOf<Uri>()
    private val _photoList = arrayListOf<Photo>()
    var uploadImages : Uri? = null
    private val _diaryList = MutableLiveData<MutableList<Diary>>()
    private val _diaryPhotoList = MutableLiveData<MutableList<Photo>>()
    private val _diary = MutableLiveData<Diary>()
    private val _hashList = MutableLiveData<MutableList<Hashtag>>()

    val diaryList : LiveData<MutableList<Diary>>
        get() = _diaryList
    val diaryPhotoList : LiveData<MutableList<Photo>>
        get() = _diaryPhotoList
    val diary : LiveData<Diary>
        get() = _diary
    val hashList : LiveData<MutableList<Hashtag>>
        get() = _hashList
    val photoUriList = MutableLiveData<ArrayList<Uri>>().apply{
        value = _photoUriList
    }
    var photoList = MutableLiveData<ArrayList<Photo>>().apply {
        value = _photoList
    }
    fun setHashtagList(list:MutableList<Hashtag>) = viewModelScope.launch {
        _hashList.value = list
    }
    fun setDiaryList(list:MutableList<Diary>) = viewModelScope.launch {
        _diaryList.value = list
    }
    fun setDiary(diary:Diary) = viewModelScope.launch {
        _diary.value = diary
    }
    fun setDiaryPhotoList(list:MutableList<Photo>) = viewModelScope.launch {
        _diaryPhotoList.value = list
    }
    fun insertPhotoUriList(uri:Uri){
        _photoUriList.add(uri)
        photoUriList.value = _photoUriList
    }
    fun insertPhotoList(photo:Photo){
        _photoList.add(photo)
        photoList.value = _photoList
    }
    fun allClearPhotoUriList(){
        _photoUriList.clear()
    }
    fun allClearPhotoList(){
        _photoList.clear()
    }
    suspend fun getDiaryList(userId:Int){
        val response = DiaryService().diaryListbyDescService(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    Log.d(TAG, "getDiaryList: ${res}")
                    if(res.success){
                        var type = object:TypeToken<MutableList<Diary?>?>() {}.type
                        var type2 = object:TypeToken<MutableList<Photo?>?>() {}.type
                        var diary = CommonUtils.parseDto<MutableList<Diary>>(res.data.get("diarys")!!,type)
                        setDiaryList(diary)

                    }else{
                        Log.d(TAG, "getDiaryList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getDiaryList: ${response.message()}")
                }
            }
        }
    }
    suspend fun getDiaryListAsc(userId:Int){
        val response = DiaryService().diaryListbyAscService(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res != null){
                    if(res.success){
                        var type = object:TypeToken<MutableList<Diary?>?>() {}.type
                        var diary = CommonUtils.parseDto<MutableList<Diary>>(res.data.get("diarys")!!,type)
                        setDiaryList(diary)
                    }else{
                        Log.d(TAG, "getDiaryListAsc: ")
                    }
                }
            }else{
                Log.d(TAG, "getDiaryListAsc: ${response.code()}")
            }
        }
    }
    suspend fun getDiaryListDate(endDate:String, startDate:String, userId:Int){
        val response = DiaryService().diaryListbyDateService(endDate, startDate, userId)
        viewModelScope.launch {
            val res = response.body()
            Log.d(TAG, "getDiaryListDate: ${res}")
            if(response.code() == 200){
                Log.d(TAG, "getDiaryListDate: ${res}")
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getDiaryListDate: ${res}")
                        var type = object :TypeToken<MutableList<Diary?>?>() {}.type
                        var diary = CommonUtils.parseDto<MutableList<Diary>>(res.data.get("diarys")!!,type)
                        setDiaryList(diary)
                    }
                }
            }
        }
    }
    suspend fun getDiaryDetail(id:Int){
        val response = DiaryService().diaryDetailService(id)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getDiaryDetail: ${res}")
                        var type = object:TypeToken<Diary?>() {}.type
                        var diary = CommonUtils.parseDto<Diary>(res.data.get("diary")!!,type)
                        setDiary(diary)
                    }
                }
            }else{
                Log.d(TAG, "getDiaryDetail: ${response.code()}")
            }
        }
    }
    suspend fun getHashTags(){
        val response = DiaryService().getHashTagService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object:TypeToken<MutableList<Hashtag>?>() {}.type
                        var hashs = CommonUtils.parseDto<MutableList<Hashtag>>(res.data.get("hashtags")!!,type)
                        setHashtagList(hashs)
                    }
                }
            }else{
                Log.d(TAG, "getHashTags: ${response.code()}")
            }
        }
    }
    /**
     * Calendar View Model
     * @author Boyeon
     * @Date 2022-03-23*/
    private val _calendarList = MutableLiveData<MutableList<Calendar>>()
    private val _calendar = MutableLiveData<MutableList<Schedule>>()
    private val _calendarWeekList = MutableLiveData<MutableList<Schedule>>()
    private val _calendarDetail = MutableLiveData<Schedule>()
    private val _walkSpace = MutableLiveData<MutableList<Walk>>()

    val calendarList : LiveData<MutableList<Calendar>>
        get() = _calendarList
    val schedule : LiveData<MutableList<Schedule>>
        get() = _calendar
    val scheduleWeekList : LiveData<MutableList<Schedule>>
        get() = _calendarWeekList
    val scheduleDetail : LiveData<Schedule>
        get() = _calendarDetail
    val walk : LiveData<MutableList<Walk>>
        get() = _walkSpace

    fun setCalendarList(list:MutableList<Calendar>) = viewModelScope.launch {
        _calendarList.value = list
    }
    fun setSchedules(list : MutableList<Schedule>) = viewModelScope.launch {
        _calendar.value = list
    }
    fun setScheduleWeek(list: MutableList<Schedule>) = viewModelScope.launch {
        _calendarWeekList.value = list
    }
    fun setScheduleDetail(schedule: Schedule) = viewModelScope.launch {
        _calendarDetail.value = schedule
    }
    fun setWalkSpace(list: MutableList<Walk>) = viewModelScope.launch {
        _walkSpace.value = list
    }
    suspend fun getCalendarListbyUser(userId:Int){
        val response = CalendarService().getCalendarListByUser(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getCalendarListbyUser: ${res}")
                        var type = object:TypeToken<MutableList<Calendar?>?>() {}.type
                        var calendar = CommonUtils.parseDto<MutableList<Calendar>>(res.data.get("schedules")!!,type)
                        Log.d(TAG, "getCalendarListbyUser: ${calendar}")
                        setCalendarList(calendar)
                    }
                }
            }else{
                Log.d(TAG, "getCalendarListbyUser: ${response.code()}")
            }
        }
    }
    
    suspend fun getCalendarListbyDate(userId:Int, datetime:String){
        val response = CalendarService().getCalendarListByDate(userId, datetime)
        Log.d(TAG, "getCalendarListbyDate: ${response.code()}")
        viewModelScope.launch {
            val res = response.body()
            if (response.code() == 200) {
                if (res != null) {
                    if (res.success) {
                        Log.d(TAG, "getCalendarListbyDate: ${res}")
                        var type = object : TypeToken<MutableList<Schedule?>?>() {}.type
                        var schedule = CommonUtils.parseDto<MutableList<Schedule>>(res.data.get("schedules")!!,type)
                        Log.d(TAG, "getCalendarListbyDate: ${schedule}")
                        setSchedules(schedule)
//                        setCalendar(calendars)
                    }
                } else {
                    Log.d(TAG, "getCalendarListbyDate: ${response.code()}")
                }
            }
        }
    }

    suspend fun getCalendarListbyPet(petId:Int){
        val response = CalendarService().getCalendarListByPet(petId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object : TypeToken<MutableList<Calendar>??>() {}.type
                        var calendar = CommonUtils.parseDto<MutableList<Calendar>>(res.data.get("schedule")!!,type)

                    }
                }
            }
        }
    }
    suspend fun getCalendarListbyWeek(userId:Int){
        val response = CalendarService().getCalendarListByWeek(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object: TypeToken<MutableList<Schedule?>?>() {}.type
                        var schedules = CommonUtils.parseDto<MutableList<Schedule>>(res.data.get("schedules")!!,type)
                        setScheduleWeek(schedules)
                    }
                }
            }
        }
    }
    suspend fun recommandWalkSapce(lat:Double, lng:Double){
        val response = CalendarService().recommandWalkSpacce(lat,lng,3.0)
        viewModelScope.launch {
            val res = response.body()
            Log.d(TAG, "recommandWalkSapce: ${res}")
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object:TypeToken<MutableList<Walk?>?>() {}.type
                        var walks = CommonUtils.parseDto<MutableList<Walk>>(res.data.get("walks")!!,type)
                        setWalkSpace(walks)
                    }
                }
            }
        }
    }
    suspend fun getCalendarDetail(id:Int){
        val response = CalendarService().getCalendarDetail(id)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object:TypeToken<Schedule?>() {}.type
                        var schedules = CommonUtils.parseDto<Schedule>(res.data.get("schedules")!!,type)
                        setScheduleDetail(schedules)
                    }
                }
            }
        }
    }
    /**
     * AI View Model
     * @Author Jueun
     * @Date 2022-03-14 13:12
     */
    lateinit var uploadedImage : Bitmap
    var uploadedImageUri : Uri? = null
    var aiType = 0
    var emotions:String = ""


    /**
     * QnABoardFragment
     * @Author Jueun
     * @Date 2022-03-24 11:37
     */
    lateinit var boardQna : Board
    lateinit var commentQna : Comment

    var writeType = 0

    private var _commentsByPostId = MutableLiveData<MutableList<Comment>>()

    val comments : LiveData<MutableList<Comment>>
        get() = _commentsByPostId

    private fun setComments(comments : MutableList<Comment>) = viewModelScope.launch {
        _commentsByPostId.value = comments
    }

    suspend fun getCommentsByPostId(postId: Int) {
        val response = BoardService().selectCommentList(postId)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if (res.data["comments"] != null && res.success) {
                        var type = object:TypeToken<MutableList<Comment>>() {}.type
                        var comment = CommonUtils.parseDto<MutableList<Comment>>(res.data["comments"]!!,type)
                        setComments(comment)
                    } else {
                        Log.e(TAG, "getCommentsByPostId: ${res.message}", )
                    }
                }
            }
        }
    }

    /**
     * Notification ViewModel
     * @author LeeBoyeon
     * @Date 2022-03-28*/

    private val _notificationList = MutableLiveData<MutableList<Notification>>()

    val notificationList : LiveData<MutableList<Notification>>
        get() = _notificationList

    fun setNotification(list: MutableList<Notification>) = viewModelScope.launch {
        _notificationList.value = list
    }

    suspend fun getNotificationAll(userId:Int) {
        val response = NotificationService().getNotificationList(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getNotificationAll: ${res}")
                        var type = object:TypeToken<MutableList<Notification?>?>() {}.type
                        var noti = CommonUtils.parseDto<MutableList<Notification>>(res.data.get("notifications")!!,type)
                        setNotification(noti)
                    }
                }
            }
        }
    }

    suspend fun getNotificationEvent(userId:Int){
        val response = NotificationService().getNotificationEventList(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getNotificationAll: ${res}")
                        var type = object:TypeToken<MutableList<Notification?>?>() {}.type
                        var noti = CommonUtils.parseDto<MutableList<Notification>>(res.data.get("notifications")!!,type)
                        setNotification(noti)
                    }
                }
            }
        }
    }

    suspend fun getNotificationNotice(userId:Int){
        val response = NotificationService().getNotificationNoticeList(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getNotificationAll: ${res}")
                        var type = object:TypeToken<MutableList<Notification?>?>() {}.type
                        var noti = CommonUtils.parseDto<MutableList<Notification>>(res.data.get("notifications")!!,type)
                        setNotification(noti)
                    }
                }
            }
        }
    }

    suspend fun getNotificationSchedule(userId:Int){
        val response = NotificationService().getNotificationScheduleList(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getNotificationAll: ${res}")
                        var type = object:TypeToken<MutableList<Notification?>?>() {}.type
                        var noti = CommonUtils.parseDto<MutableList<Notification>>(res.data.get("notifications")!!,type)
                        setNotification(noti)
                    }
                }
            }
        }
    }



    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * History ViewModel
     * @author Jiwoo
     * @since 04.04.22.
     */
    private val _historyList = MutableLiveData<MutableList<History>>()

    val historyList : LiveData<MutableList<History>>
        get() = _historyList

    fun setHistory(list: MutableList<History>) = viewModelScope.launch {
        _historyList.value = list
    }

    suspend fun getHistoryList (userId: Int){
        val response = HistoryService().selectAllHistory(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res.data["historys"] != null && res.success) {
                        val type = object : TypeToken<MutableList<History>>() {}.type
                        val historyList: MutableList<History> = CommonUtils.parseDto(res.data["historys"]!!, type)
                        setHistory(historyList)
                    } else {
                        Log.e(TAG, "getHistoryList: ${res.message}", )
                    }
                }
            }
        }
    }

}
