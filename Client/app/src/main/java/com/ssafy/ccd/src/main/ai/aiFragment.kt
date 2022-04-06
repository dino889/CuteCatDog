package com.ssafy.ccd.src.main.ai

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
//import com.kakao.kakaolink.v2.KakaoLinkResponse
//import com.kakao.kakaolink.v2.KakaoLinkService
//import com.kakao.message.template.ButtonObject
//
//import com.kakao.message.template.ContentObject
//import com.kakao.message.template.LinkObject
//import com.kakao.network.ErrorResult
//import com.kakao.network.callback.ResponseCallback
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.template.model.*

import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAiBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.History
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.main.information.InformationActivity
import com.ssafy.ccd.src.main.information.InformationMainFragment
import com.ssafy.ccd.src.network.service.BoardService
import com.ssafy.ccd.src.network.service.HistoryService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Response
import java.io.*

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.log
import kotlin.math.min
import kotlin.math.round

private const val TAG = "aiFragment"
open class aiFragment : BaseFragment<FragmentAiBinding>(FragmentAiBinding::bind,R.layout.fragment_ai) {
    // mainActivity 관련 객체
    private lateinit var mainViewModels: MainViewModels
    private lateinit var mainActivity: MainActivity
    private lateinit var contentResolver : ContentResolver
    private lateinit var intent: Intent

    // 로딩바
    lateinit var progressDialog: ProgressDialog
    private var storageReference: StorageReference? = null

    // binding 객체
    private lateinit var imageView:ImageView
    private lateinit var tvEmotion:TextView
    private lateinit var tvSolution:TextView
    private lateinit var ivBack : ImageView
    private lateinit var clTrain : ConstraintLayout
    private lateinit var clCon : ConstraintLayout
    private lateinit var clMore : ConstraintLayout

    // 이미지 관련 객체
    private var imageSizeX = 0
    private var imageSizeY = 0
    private lateinit var bitmap: Bitmap

    // TnesorFlow 관련 객체
    private lateinit var tflite: Interpreter
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var probabilityProcessor: TensorProcessor
    private lateinit var outputProbabilityBuffer: TensorBuffer
    private lateinit var labels:List<String>

    // 결과
    private lateinit var result:String
    var filePath:String = ""
    private var isFabOpen = false

    // history 등록 결과
    private var isRegistered = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity =  (requireActivity() as MainActivity)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            setInstance()
            setListener()
        }

        progressDialog.show()
        setInit()

        insertHistory()

    }

    private fun setListener() {
        setFabClickEvent()
        // 뒤로가기 버튼
        ivBack.setOnClickListener {
            (requireActivity() as MainActivity).onBackPressed()

//            this@aiFragment.findNavController().navigate(R.id.homeFragment)
        }

        clTrain.setOnClickListener {
            if (mainViewModel.aiType == 0) {
                // 강아지 훈련
                intent.putExtra("type", 0)
                startActivity(intent)
            }else{
                // 고양이 훈련
                intent.putExtra("type", 2)
                startActivity(intent)
            }
        }

        clCon.setOnClickListener {
            if (mainViewModel.aiType == 0) {
                // 강아지 교감
                intent.putExtra("type", 1)
                startActivity(intent)
            }else{
                // 고양이 교감
                intent.putExtra("type", 3)
                startActivity(intent)
            }
        }

        clMore.setOnClickListener {
            this@aiFragment.findNavController().navigate(R.id.informationMainFragment)
        }
    }

    private fun setInstance() {
        // mainActivity 관련 객체

        contentResolver = mainActivity.contentResolver
        mainViewModels = mainActivity.mainViewModels
        intent = Intent(requireActivity(), InformationActivity::class.java)

        // 로딩바
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage("Please wait.....")

        // Firebase 관련 객체
        storageReference = FirebaseStorage.getInstance().getReference("Animals")

        // Android Binding 객체
        imageView = binding.fragmentAiImage
        Glide.with(requireContext())
            .load(mainViewModels.uploadedImageUri)
            .into(imageView)
//            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
//        imageView.setImageURI(mainViewModels.uploadedImageUri)
        tvEmotion = binding.fragmentAiResultEmotion
        tvSolution = binding.fragmentAiResultSolution
        ivBack = binding.fragmentAiIvBack
        clTrain = binding.fragmentAiClTra
        clCon = binding.fragmentAiClCon
        clMore = binding.fragmentAiClMore

        // TensorFlow 객체
        try {
            tflite = Interpreter(loadmodelfile(mainActivity))
        } catch (e: Exception) {
            e.printStackTrace()
            showCustomToast(e.message.toString())
        }

        try {
            labels = FileUtil.loadLabels(mainActivity, "labels.txt")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showCustomToast(e.message.toString())
        }

        // Bitmap 설정
        bitmap = mainViewModels.uploadedImage
    }

    private fun setInit() {
        // ErrorCheck (객체 확인)
        if(storageReference == null) {
            Log.e("ERROR", "Firebase에서 문제가 발생하였습니다.")
            showCustomToast("Firebase에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        // 이미지 Uri 확인
        if(mainViewModels.uploadedImageUri == null){
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        runBlocking {
            val imageTensorIndex2 = 0
            val imageShape2 = tflite.getInputTensor(imageTensorIndex2).shape() // {1, height, width, 3}

            imageSizeY = imageShape2[1]
            imageSizeX = imageShape2[2]
            val imageDataType2 = tflite.getInputTensor(imageTensorIndex2).dataType()
            val probabilityTensorIndex2 = 0
            val probabilityShape2 = tflite.getOutputTensor(probabilityTensorIndex2).shape() // {1, NUM_CLASSES}
            val probabilityDataType2 = tflite.getOutputTensor(probabilityTensorIndex2).dataType()

            inputImageBuffer = TensorImage(imageDataType2)
            outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape2, probabilityDataType2)
            probabilityProcessor = TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build()
            inputImageBuffer = loadImage(bitmap)
            tflite.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())

            // 결과 출력
            val labeledProbability: Map<String, Float> = TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer)).mapWithFloatValue
            val maxValueInMap = Collections.max(labeledProbability.values)

            labeledProbability.entries.forEach { entry ->
                if (entry.value == maxValueInMap) {
                    result = entry.key
                    tvEmotion.text = result
                }
            }

            tvSolution.text = getSolution(mainViewModel.aiType, result)
        }

        progressDialog.dismiss()
    }


    private fun loadmodelfile(activity: Activity): MappedByteBuffer {
        // 개인지 고양인지에 따라서 AI 모델을 달리 가져온다.
        val fileDescriptor = if(mainViewModel.aiType == 0 )
            activity.assets.openFd("model.tflite")
        else activity.assets.openFd("model2.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startoffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
    }

    private fun getPreprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    }

    private fun getPostprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(PROBABILITY_MEAN,PROBABILITY_STD)
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {
        inputImageBuffer.load(bitmap)
        val cropSize = min(bitmap.width, bitmap.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(getPreprocessNormalizeOp())
            .build()

        return imageProcessor.process(inputImageBuffer)
    }

    fun getSolution(type:Int, str:String) : String{
        return when(type){
            0->{
                when(str){
                    "화났어요" -> "어쩜, 우리 친구가 많이 화가난 상태네요. 우선 화를 풀기 위해 간식을 주는 행위는 더 안좋은 상황을 만들 수 있습니다. 그러니 간식보다는 진정될 때 까지 흥분을 가라앉히게 만들고 교육을 진행하는 것이 바람직합니다. 화가 났을때는 칭찬이나 쓰다듬으면 잘했다고 칭찬을 받는 줄 알고 같은 행동을 반복할 수 있으니 주의하세요!"
                    "울고싶어요" -> "강아지가 우는 이유 중 하나는 강아지의 대화 방법이라고 할 수 있습니다. 무엇인가 원하고 있거나 바라는 것이 있을 수 있고 불안하거나 두려울때 또는 매우 흥분 했을때도 울기도 합니다. 원인을 먼저 잘 파악하는게 중요하죠. 어떤 경우에 얼마동안 우는지 잘 관찰하여 무엇을 원하는지 파악하려고 노력해보세요. 원인이 무엇이 되었던간에 우는 강아지를 혼내거나 야단치는 것은 문제 해결에 도움이 되지 않으니 지양하시길 바랍니다. 그리고 해결해줄 수 있는 부분은 적절한 훈련을 통해 보상으로 주고 무작정 요구를 받아주는 형식으로 진행되서는 안된답니다. 또한 너무 관심을 준다면 그 행위를 반복할 수 있으니 적절하게 외면하는것도 하나의 방법입니다."
                    "기뻐요" -> "오늘은 매우 즐거운가봐요! 너무 기분이 좋아보여요. 강아지가 기분 좋을때는 적절한 산책과 훈련을 통해 교감도를 높이고 다양한 활동을 하면서 스트레스를 풀어준다면 향후 반려동물의 감정에도 긍정적인 영향을 미친답니다."
                    "안정감있어요" -> "매우 안정감 있는 상태로군요. 현재 상태는 주변 환경을 신뢰한다고 할 수 있어요. 긴장을 풀고 편안한 상태로 지금과 같은 환경을 유지하고 어떤 상황일때 안정감을 느끼는지 기억하는게 좋아요. 추후 흥분하거나 슬퍼할때 지금 상태와 같은 환경을 조성해주세요.\n\n편안함을 느낄때 반려동물이 취하는 행동\n배를 보이고 눕는다.\n몸을 동그랗게 말고 잔다.\n옆으로 눕는다."
                    "슬퍼요" -> "강아지가 혹시 무기력하게 움직이지 않나요?\n이럴때 좋아하는 간식을 주어도 똑같은지 확인해보세요.\n\n보호자의 말을 잘 듣지 않나요?\n갑자기 반려동물이 말을 듣지 않는 것은 슬픔을 느끼기 때문이라고해요.\n\n혹시 반려동물이 파괴적인 행동을 보이나요?\n슬픔이 지속되면 사람도 스트레스를 받기 마련이죠. 반려동물도 마찬가지입니다. 감정을 해소하고싶어 가구를 뜯는다거나 대소변을 아무데나 하는 행위는 슬프거나 스트레스를 받는 행위라고합니다.\n\n강아지가 슬픔을 느낄때는 가볍게 교감을 시도하거나 산책을 통해 기분 전환을 시켜주세요. 좋아하는 간식을 주며 훈련을 한다거나 안정감이 드는 환경을 조성해주는 것이 좋답니다."
                    else -> "잘못된 사진입니다. 솔루션을 해줄 수 없어요 \uD83D\uDE25"
                }
            }
            1->{
                when(str){
                    "화났어요" -> "우리의 집사는 고양이 심리 상태를 파악하기 어려워요. 가끔은 가만히 있다가도 하악질을 하거나 괴상한 소리를 내기도 하죠. 고양이의 귀가 납작하게 눕혀져있다면 매우 기분이 나쁜 상태입니다. 반려묘가 화난 상태라면 큰소리를 내지 말고 안정된 상태가 될 수 있도록 하는게 중요하답니다. 또한 눈을 응시하는것은 결투장을 던지는 것과 같은 행위이므로 자연스럽게 다른곳을 보거나 눈을 천천히 깜빡여 애정의 메세지를 보내세요. 가장 좋은 방법은 반려묘에게 혼자만의 공간과 시간을 주는것이 가장 효과적입니다."
                    "울고싶어요" -> "고양이가 울때는 관심이 필요하거나 발정기에 우는 경향이 많아요. 또는 아프거나 배고플때도 운답니다. 이럴때는 감정 원인에 따라 대처법이 다릅니다. 관심이 필요해서 운다면 놀아주고 교감을 시도하면 좋지만 관심을 끌기 위한 울음소리를 줄이고 싶다면 울음소리르 낼 때 행동을 멈추거나 외면하고 울음소리를 멈추면 다시 놀아주고를 반복해야합니다. 이때 짜증이나 화난 행동도 관심으로 받아들이기 때문에 인내심을 갖고 일관성 있게 교육해야합니다. 또한 밥그릇, 물그릇을 갈아주고 화장실을 청소하는 등 안정시킬 수 있는 환경을 조성해주세요. 생후 6개월 이상의 고양이가 발정이 났을때 우는 경우도 있으니 이 경우에는 중성화 수술을 진행해야합니다."
                    "기뻐요" -> "고양이는 기분 좋을때 꾹꾹이를 하거나 꼬리를 수직으로 세우는 행동을 합니다. 또는 곁에 와서 눕거나 몸을 비비는 행위 골골 거리며 행복한 상태를 소리로 표현하기도 한답니다. 이러한 상황을 기억하고 슬프거나 무기력한 상태라면 지금과 같은 환경을 조성해주세요."
                    "안정감있어요" -> "고양이는 낮은 곳 보다는 높은 곳에서 안정감을 느낀다고 해요. 조용하고 높은 장소를 마련해준다면 안정감을 느끼기 좋습니다. 그리고 고양이는 청결한 동물이라 화장실이 깨끗하고 쾌적하게 정리되어 있다면 안정감을 느끼니 반려묘 주변 환경의 위생에 신경써주세요!"
                    "슬퍼요" -> "고양이가 쓸쓸히 아래를 보거나 작은 목소리로 가느다랗게 우나요? 쭙쭙이를 하거나 꼬리를 축 늘어뜨린다면 슬픈 상태일 수 있습니다. 따뜻한 스킨쉽을 하면서 좋아하는 부위를 가볍게 마사지하거나 맛있는 간식을 주는 등 애정표현을 해주세요. 고양이도 외로움을 타기 때문이죠. 재미있는 놀이를 진행해도 아주 좋은 방법입니다. 하지만 반려묘의 상태를 확인하는 것이 필수라는 점! 아프거나 상태가 안좋을 수 있으니 반응과 행동을 잘 살펴보세요."
                    else -> "잘못된 사진입니다. 솔루션을 해줄 수 없어요 \uD83D\uDE25"
                }
            }
            else -> {
                ""
            }
        }
    }

    fun shareInstagram() {
        var type: String? = "image/*"
        var realPath = mainViewModels.uploadedImageUri?.let { mainActivity.getPath(it) }
        var share = Intent(Intent.ACTION_SEND)
        share.setType(type)
        var media = File(realPath)
        var uri = FileProvider.getUriForFile(requireContext(),"com.ssafy.ccd",media)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share,"Share to"))
    }
    private fun shareKakao(){
        Log.d(TAG, "shareKakao: ${filePath}")
        val defaultFeed = FeedTemplate(
            content = Content(
                title = "내 반려동물 감정분석은?",
                description = "친구의 반려동물 감정은 ${binding.fragmentAiResultEmotion.text.toString()}입니다 :)",
                imageUrl = filePath.toString(),
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            itemContent = ItemContent(
                profileText = "ㅋㅋㄷ",
                profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/cutecatdog-32527.appspot.com/o/logo%2Flogo.png?alt=media&token=e13450dd-0911-4558-bd8f-265fe5d16165",
            ),
            null,
            buttons = listOf(
                Button(
                    "웹으로 보기",
                    Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Button(
                    "앱으로 보기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )


        // 카카오톡 설치여부 확인
        if (LinkClient.instance.isKakaoLinkAvailable(requireContext())) {
            // 카카오톡으로 카카오링크 공유 가능
            LinkClient.instance.defaultTemplate(requireContext(), defaultFeed) { linkResult, error ->
                if (error != null) {
                    Log.e(TAG, "카카오링크 보내기 실패", error)
                }
                else if (linkResult != null) {
                    Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                    startActivity(linkResult.intent)

                    // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                    Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                    Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultFeed)

            // CustomTabs으로 웹 브라우저 열기

            // 1. CustomTabs으로 Chrome 브라우저 열기
            try {
                KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
            } catch(e: UnsupportedOperationException) {
                // Chrome 브라우저가 없을 때 예외처리
            }

            // 2. CustomTabs으로 디바이스 기본 브라우저 열기
            try {
                KakaoCustomTabsClient.open(requireContext(), sharerUrl)
            } catch (e: ActivityNotFoundException) {
                // 인터넷 브라우저가 없을 때 예외처리
            }
        }
    }
    fun setFabClickEvent() {
            binding.fragmentAiFabMain.setOnClickListener {
                toggleTab()
            }
            binding.fragmentAiShareSns.setOnClickListener {
                //instagram
                shareInstagram()
            }
            binding.fragmentAiShareKakao.setOnClickListener {
                shareKakao()
            }
            binding.fragmentAiToDiary.setOnClickListener {
                mainViewModels.emotions = binding.fragmentAiResultEmotion.text.toString()

                var check = 3
                val flag = bundleOf("flag" to check)
                this@aiFragment.findNavController().navigate(R.id.diaryWriteFragment, flag)
                Log.d(TAG, "showBottomShareDialog: eng?")
            }
        }

        fun toggleTab() {

            if (isFabOpen) {
                ObjectAnimator.ofFloat(binding.fragmentAiShareSns, "translationY", 0f)
                    .apply { start() }
                ObjectAnimator.ofFloat(binding.fragmentAiShareKakao, "translationY", 0f)
                    .apply { start() }
                ObjectAnimator.ofFloat(binding.fragmentAiToDiary, "translationY", 0f)
                    .apply { start() }
                ObjectAnimator.ofFloat(binding.fragmentAiToDiary, View.ROTATION, 70f, 0f)
                    .apply { start() }
            } else {
                ObjectAnimator.ofFloat(binding.fragmentAiShareSns, "translationY", -600f)
                    .apply { start() }
                ObjectAnimator.ofFloat(binding.fragmentAiShareKakao, "translationY", -400f)
                    .apply { start() }
                ObjectAnimator.ofFloat(binding.fragmentAiToDiary, "translationY", -200f)
                    .apply { start() }
                ObjectAnimator.ofFloat(binding.fragmentAiToDiary, View.ROTATION, 0f, 70f)
                    .apply { start() }
            }
            isFabOpen = !isFabOpen

    }


    private fun insertHistory() {   // setInit() 뒤에 호출
        val imgUri = mainViewModels.uploadedImageUri
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

        val fileName = if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY) {
            ""
        } else{
            val timeName = System.currentTimeMillis().toString()
            "${userId}/${timeName}."+ getFileExtension(imgUri)
        }
        if(fileName == "") {
            showCustomToast("사진을 선택해 주세요")
        } else {
            val history = History(
                userId = userId,
                emotion = result,
                photoPath = fileName,
                datetime = System.currentTimeMillis().toString())

            var response : Response<Message>

            runBlocking {
                response = HistoryService().insetHistory(history)
                uploadUserImgToFirebase(history)
            }

            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.success == true && res.data["isSuccess"] == true) {
                        isRegistered = true
                    } else if(res.data["isSuccess"] == false) {
                        showCustomToast("history 등록 실패")
                        Log.e(TAG, "insertHistory: ${res.message}", )
                    } else {
                        Log.e(TAG, "insertHistory: 서버 통신 실패 ${res.message}", )
                    }
                }
            }

        }
    }


    /**
     * 이미지 firebase storage 업로드
     */
    private fun uploadUserImgToFirebase(history: History) {
        if(mainViewModels.uploadedImageUri == null || mainViewModels.uploadedImageUri == Uri.EMPTY) {
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        val pathString = history.photoPath.substring(history.photoPath.lastIndexOf("/") + 1, history.photoPath.length)

        val storageReferenceChild = FirebaseStorage.getInstance()
            .getReference("${history.userId}")
            .child(pathString)

        storageReferenceChild.putFile(mainViewModels.uploadedImageUri!!)
            .addOnSuccessListener{
                storageReferenceChild.downloadUrl
                    .addOnSuccessListener {
                        isRegistered = true
                        filePath = it.toString()
                        Log.d(TAG, "uploadUserImgToFirebase: $it")
                    }
            }
    }

    /**
     * 이미지 파일 확장자 찾기
     */
    private fun getFileExtension(uri: Uri?): String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        contentResolver = mainActivity.contentResolver
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNavi(false)
    }


    companion object {
        // TensorFlow 관련 Final 값
        private const val IMAGE_MEAN = 0.0f
        private const val IMAGE_STD = 1.0f
        private const val PROBABILITY_MEAN = 0.0f
        private const val PROBABILITY_STD = 255.0f
    }
}
