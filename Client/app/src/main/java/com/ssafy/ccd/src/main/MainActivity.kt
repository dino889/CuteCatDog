package com.ssafy.ccd.src.main

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.location.*
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseActivity
import com.ssafy.ccd.databinding.ActivityMainBinding
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import java.text.SimpleDateFormat
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.network.api.FCMApi
import retrofit2.Response
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.youtube.player.internal.e
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileUtils.copyFile
import com.google.type.LatLng
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.util.LoadingDialog
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.lang.Runnable
import java.util.*
import kotlin.math.round
import android.R.attr.mimeType





private const val TAG = "MainActivity_ccd"
class MainActivity :BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    // 카메라 모드
    private var cameraMode = 0

    // 카메라, 저장장소 권한
    private val CAMERA = arrayOf(Manifest.permission.CAMERA)
    private val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val CAMERA_CODE = 98
    private val STORAGE_CODE = 99
    private val GALLERY_CODE = 10
    private val DIARY_CODE = 20
    // Dialog
    private lateinit var photoDialog:Dialog
    private lateinit var photoDialogView: View

    // viewModel
    lateinit var mainViewModels: MainViewModels

    private var mauth = FirebaseAuth.getInstance()

    // 현재 위치 locationManager
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    // 위치 권한
    private val LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val LOCATION_CODE = 100

    // 로딩창
    lateinit var loadingDialog: LoadingDialog

    override fun onRestart() {
        super.onRestart()
        if(checkPermissionForLocation(this)) {
            startLocationUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setInit()
        setNavigation()
        setInstance()
        setListener()

        initFcm()

        if(checkPermissionForLocation(this)) {
            startLocationUpdates()
        }
    }

    /**
     * @author Jueun
     * 로딩 다이얼로그를 10초간 띄운다
     */
    fun showLoadingDialog(){
        if(loadingDialog.isShowing) return

        CoroutineScope(Dispatchers.Main).async {
            loadingDialog.setCancelable(false)
            loadingDialog.show()

            delay(15000)
            if(loadingDialog.isShowing) {
                showCustomToast("조금 뒤에 다시 시도해주세요.")
                hideLoadingDialog()
            }
        }

    }

    fun hideLoadingDialog(){
        loadingDialog.dismiss()
    }

    /**
     * 초기작업이 필요한 부분
     */
    private fun setInit() {
        binding.activityMainBottomNavigationView.background = null
        binding.activityMainBottomNavigationView.menu.getItem(2).isEnabled = false
    }

    /**
     * BottomNavigation 관련 설정
     */
    private fun setNavigation() {
        // 네비게이션 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_navHost) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바인딩
        NavigationUI.setupWithNavController(binding.activityMainBottomNavigationView, navController)
    }

    /**
     * BottomNavigation show/hide 설정
     * true - hide
     * false - show
     */
    fun hideBottomNavi(state : Boolean) {
        if(state) {
            binding.bottomAppBar.visibility = View.GONE
            binding.activityMainFabCam.visibility = View.GONE
            val param = binding.activityMainNavHost.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0, 0)
            binding.activityMainNavHost.layoutParams = param
        } else {
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.activityMainFabCam.visibility = View.VISIBLE
            val param = binding.activityMainNavHost.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0, round(60 * resources.displayMetrics.density).toInt())
            binding.activityMainNavHost.layoutParams = param
        }
    }

    /**
     * 인스턴스를 정의하는 함수
     */
    @SuppressLint("InflateParams")
    private fun setInstance() {
        // photoDialog
        photoDialogView = LayoutInflater.from(this).inflate(R.layout.fragment_ai_dialog,null)
        photoDialog = Dialog(this)
        photoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        photoDialog.setContentView(photoDialogView)
        loadingDialog = LoadingDialog(this)

        // mainViewModel
        mainViewModels = ViewModelProvider(this).get(MainViewModels::class.java)
    }

    /**
     * 리스너를 정의하는 함수
     */
    private fun setListener() {
        // AI Dialog
        binding.activityMainFabCam.setOnClickListener {
            photoDialog.show()
        }

        photoDialogView.findViewById<ImageButton>(R.id.fragment_ai_dialog_cancle).setOnClickListener {
            photoDialog.dismiss()
        }

        photoDialog.findViewById<ConstraintLayout>(R.id.dialog_ai_BtnCam).setOnClickListener {
            callCamera()
        }

        photoDialog.findViewById<ConstraintLayout>(R.id.dialog_ai_BtnUpload).setOnClickListener {
            getAlbum(STORAGE_CODE)
        }
    }


    /**
     * @author Jueun
     * @param permissions 권한 목록
     * @param type 권한 코드
     * @return Boolean 허용/거부
     * 권한을 확인하는 함수
     */
    fun checkPermission(permissions: Array<out String>, type: Int): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }

    /**
     * @author Jueun
     * @param permissions 권한 목록
     * @param requestCode 요청 코드
     * 권한 요청 후 결과값을 표시하는 함수
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("카메라 권한을 승인해 주세요.")
                    }
                }
            }

            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("저장소 권한을 승인해 주세요.")
                    }
                }
            }

            GALLERY_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("저장소 권한을 승인해 주세요.")
                    }
                }
            }

            DIARY_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("저장소 권한을 승인해 주세요.")
                    }
                }
            }
            LOCATION_CODE -> {
                for(grant in grantResults) {
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("위치 권한을 승인해 주세요.")
                        Log.d(TAG, "onRequestPermissionsResult: ")
                    } else if(grant == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates()
                    }
                }
            }
        }

    }

    /**
     * @author Jueun
     * 카메라, 저장공간 등을 불러오고 결과값을 받을 때
     * onActiviryResult로 return 된다.
     * 필요한 데이터를 활용하면 되지만,
     **** Warning :: RequestCode에 따라 분기처리를 잘 할 것!! ****
     */
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (data?.extras?.get("data") != null) {
                        mainViewModels.uploadedImage = data.extras?.get("data") as Bitmap
                        Log.d(TAG, "onActivityResult: ${(data.extras?.get("data") as Bitmap)}")
                        mainViewModels.uploadedImageUri = saveFile(randomFileName(), "image/jpg", mainViewModels.uploadedImage)

                        // 이미지 검사
                        if(mainViewModels.uploadedImageUri == null) showCustomToast("이미지가 정상적으로 로드 되지 않았습니다.")
                        else {
                            // 이미지를 정상적으로 불러들였다면, AI fragment 페이지로 이동한다.
                                checkTheType()
                                if(photoDialog.isShowing) photoDialog.dismiss()
//                            binding.activityMainNavHost.findNavController().navigate(R.id.aiSelectFragment)

                        }
                    }
                }

                STORAGE_CODE -> {
                    mainViewModels.uploadedImageUri = data?.data

                    Log.d(TAG, "onActivityResult: ${data?.data}")
                    // 이미지 검사
                    if(mainViewModels.uploadedImageUri == null) showCustomToast("이미지가 정상적으로 로드 되지 않았습니다.")
                    else {
                        mainViewModels.uploadedImage = MediaStore.Images.Media.getBitmap(contentResolver, mainViewModels.uploadedImageUri)

                        // 이미지를 정상적으로 불러들였다면, AI fragment 페이지로 이동한다.
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.activity_main_navHost, aiSelectFragment())
//                            .addToBackStack(null)
//                            .commit()

                        checkTheType()
                        photoDialog.dismiss()
                    }
                }

                GALLERY_CODE -> {
                    mainViewModels.uploadedImageUri = data?.data
                    // 이미지 검사
                    if (mainViewModels.uploadedImageUri == null) showCustomToast("이미지가 정상적으로 로드 되지 않았습니다.")
                    else {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mainViewModels.uploadedImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                                    contentResolver, mainViewModels.uploadedImageUri!!
                                ))
                            } else {
                                mainViewModels.uploadedImage = MediaStore.Images.Media.getBitmap(
                                    contentResolver, mainViewModels.uploadedImageUri)
                            }
                        } catch ( e: IOException) {
                            e.printStackTrace();
                        }
                    }
                }

                DIARY_CODE -> {
//                    mainViewModels.photoUriList.observe(this, {
//                        data?.data?.let { it1 -> it.add(it1) }
//                        mainViewModels.setPhotoUriList(it)
//                    })
                    mainViewModels.uploadImages = data?.data
                    data?.data?.let { mainViewModels.insertPhotoUriList(it) }
                    if (mainViewModels.uploadImages == null) showCustomToast("이미지가 정상적으로 로드 되지 않았습니다.")
                    else {
                        Log.d(TAG, "onActivityResult: this?")
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                Log.d(TAG, "onActivityResult: here?")
                                mainViewModels.uploadedImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                                    contentResolver, mainViewModels.uploadImages!!
                                ))
                            } else {
                                Log.d(TAG, "onActivityResult: here2?")
                                mainViewModels.uploadedImage = MediaStore.Images.Media.getBitmap(
                                    contentResolver, mainViewModels.uploadImages)
                            }
                        } catch ( e: IOException) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun checkTheType(){
        showLoadingDialog()
        Log.d(TAG, "checkTheType: ${mainViewModels.uploadedImageUri!!}")
        val file = File(mainViewModels.uploadedImageUri!!.path)
        val fileExtension = contentResolver.getType(mainViewModels.uploadedImageUri!!)
        var inputStream : InputStream? = null
        try{
            inputStream = this.contentResolver.openInputStream(mainViewModels.uploadedImageUri!!)
        }catch (e:IOException){
            e.printStackTrace()
        }
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream)
        val requestBody = RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray())
        val uploadFile = MultipartBody.Part.createFormData("file","${file.name}.${fileExtension?.substring(6)}",requestBody)

        GlobalScope.launch {
            val response = PetService().getAipetType(uploadFile)
            val res = response.body()
            Log.d("TAG", "checkTheType: $res ${response.code()}")
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var kinds = res.data["kind"]

                        if(kinds!! == "cat"){
                            mainViewModels.aiType = 1
                        }else if(kinds == "dog"){
                            mainViewModels.aiType = 0
                        }else{
                            mainViewModels.aiType = 2
                        }
                        runOnUiThread(Runnable {
                            hideLoadingDialog()
                            binding.activityMainNavHost.findNavController().navigate(R.id.aiSelectFragment)
                        })
                    }
                }
            }
        }
    }
    /**
     * @author Boyeon
     * 갤러리에서 사진가져올때 경로 꺼내오기
     * */
    fun getPath(uri:Uri):String{
        var result = ""
        var cursor = contentResolver.query(uri,null,null,null,null)
        if(cursor == null){
            result = uri.path.toString()
        }else{
            cursor.moveToFirst()
            var idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
    /**
     * @author Jueun
     * 카메라 요청을 하였을 때 사용하는 함수
     * 권한을 확인 한 후 권한이 없다면 카메라와
     * 저장공간에 대한 허용을 요청한다
     */
    private fun callCamera()
    {
        if (checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }
    }

    /**
     * @author Jueun
     * @param FileName 저장할 파일 이름
     * @param mimeType 타입 설정
     * @param bitmap 저장할 사진 비트맵
     * @return Uri 정보를 리턴한다.
     * 사진을 디바이스에 저장하고 해당 이미지의 Uri를 반환한다.
     */
    private fun saveFile(FileName: String, mimeType: String, bitmap: Bitmap): Uri?
    {
        val CV = ContentValues()
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, FileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
            CV.put(MediaStore.Downloads.DISPLAY_NAME, "$FileName.JPG")
            CV.put(MediaStore.Downloads.MIME_TYPE, mimeType)
        }

        try {
            var uri : Uri? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, CV)
            }else uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)

            if (uri != null) {
                val scriptor = contentResolver.openFileDescriptor(uri, "w")
                Log.d(TAG, "saveFile: ${scriptor}")

                if (scriptor != null) {
                    val fos = FileOutputStream(scriptor.fileDescriptor)
                    Log.d(TAG, "saveFile: ${fos}")
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        CV.clear()
                        CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, CV, null, null)
                    }
                }
                return uri
            }
        }catch (e : Exception){
            Log.d(TAG, "saveFile: ${e.printStackTrace()}")
        }

        return null
    }

    /**
     * @author Jueun
     * 랜덤한 사진명 생성하는 함수
     */
    @SuppressLint("SimpleDateFormat")
    private fun randomFileName(): String {
        return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
    }

    /**
     * @author Jueun
     * 앨범에서 사진을 가져오는 함수
     */
    fun getAlbum(code:Int)
    {
        if (checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, code)
        }
    }

    /**
     * @author Jueun
     * bottomNavigation 숨기기
     */
    fun hideBottomAppBar(){
        binding.bottomAppBar.visibility = View.GONE
        binding.activityMainFabCam.visibility = View.GONE
        binding.isGone = true
        val param = binding.activityMainNavHost.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0,0,0, 0)
        binding.activityMainNavHost.layoutParams = param
    }

    /**
     * @author Jueun
     * bottomNavigation 보이기
     */
    fun showBottomAppBar(){
        binding.bottomAppBar.visibility = View.VISIBLE
        binding.activityMainFabCam.visibility = View.VISIBLE
        binding.isGone = false
        val param = binding.activityMainNavHost.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0,0,0, round(60 * resources.displayMetrics.density).toInt())
        binding.activityMainNavHost.layoutParams = param
    }

    /**
     * @author Jueun
     * bottomNavigation mainActivty resume 될 때
     * bottomNav가 열리도록 함.
     */
    override fun onResume() {
        super.onResume()
        showBottomAppBar()
    }

//    /**
//     * 키보드 숨기기
//     */
//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        val view = currentFocus
//        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
//                "android.webkit."
//            )
//        ) {
//            val scrcoords = IntArray(2)
//            view.getLocationOnScreen(scrcoords)
//            val x = ev.rawX + view.getLeft() - scrcoords[0]
//            val y = ev.rawY + view.getTop() - scrcoords[1]
//            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
//                Context.INPUT_METHOD_SERVICE
//            ) as InputMethodManager).hideSoftInputFromWindow(
//                this.window.decorView.applicationWindowToken, 0
//            )
//        }
//
//        return super.dispatchTouchEvent(ev)
//    }

    /**
     * FCM 토큰 수신 및 채널 생성
     */
    private fun initFcm() {
        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            // token log 남기기
            Log.d(TAG, "token: ${task.result?:"task.result is null"}")
            uploadToken(task.result!!, ApplicationClass.sharedPreferencesUtil.getUser().id)
        })

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channel_id, "ssafy")
        }
    }

    /**
     * Fcm Notification 수신을 위한 채널 추가
     */
    @RequiresApi(Build.VERSION_CODES.O)
    // Notification 수신을 위한 체널 추가
    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT // or IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)

        val notificationManager: NotificationManager
                = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val channel_id = "ssafy_channel"
        fun uploadToken(token:String, userId: Int) {
            val storeService = ApplicationClass.retrofit.create(FCMApi::class.java)

            var response : Response<Message>
            runBlocking {
                response = storeService.uploadToken(token, ApplicationClass.sharedPreferencesUtil.getUser().id)
            }
            val res = response.body()
            if(response.code() == 200) {
                if(res != null) {
                    if(res.data["isSuccess"] == true && res.message == "토큰 등록 성공") {
                        Log.d(TAG, "uploadToken: $token")
                    } else {
                        Log.d(TAG, "uploadToken: ${res.message}")
                    }
                }
            } else {
                Log.e(TAG, "uploadToken: 토큰 정보 등록 중 통신 오류")
            }
        }
    }

    /**
     * 위치 권한
     */
    fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(this, LOCATION, LOCATION_CODE)
                false
            }
        } else {
            true
        }
    }

    fun startLocationUpdates() {
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        Log.d(TAG, "onLocationChanged: lat = ${mLastLocation.latitude} / lng = ${mLastLocation.longitude}")

        mainViewModels.setUserLoc(location, getAddress(location))

        runBlocking {
            mainViewModels.getLocPostListByUserLoc(mainViewModels.userLoc!!)
        }
    }

    fun getAddress(position: Location) : String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(position.latitude, position.longitude, 1).first()
            .getAddressLine(0)

        Log.d(TAG, "Address, $address")
        return address
    }



    /**
     * firebase storage 익명성
     */
    override fun onStart() {
        super.onStart()
        val user = mauth.currentUser

        if(user!=null){

        }else{
            signInAnonymously()
        }
    }
    private fun signInAnonymously(){
        mauth.signInAnonymously().addOnSuccessListener(this, OnSuccessListener<AuthResult>() {

        }).addOnFailureListener(this) {
            Log.e(TAG, "signInAnonymously: ")
        }
    }

}