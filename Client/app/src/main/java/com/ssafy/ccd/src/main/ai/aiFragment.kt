package com.ssafy.ccd.src.main.ai

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAiBinding
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import org.tensorflow.lite.DataType
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
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.math.min

open class aiFragment : BaseFragment<FragmentAiBinding>(FragmentAiBinding::bind,R.layout.fragment_ai) {
    // mainActivity 관련 객체
    private lateinit var mainViewModels: MainViewModels
    private lateinit var mainActivity: MainActivity
    private lateinit var contentResolver : ContentResolver

    // 로딩바
    lateinit var progressDialog: ProgressDialog
    private var storageReference: StorageReference? = null

    // binding 객체
    private lateinit var imageView:ImageView
    private lateinit var tvEmotion:TextView

    // 이미지 관련 객체
    private var imageSizeX = 0
    private var imageSizeY = 0
    private val imageTensorIndex = 0
    private lateinit var bitmap: Bitmap

    // TnesorFlow 관련 객체
    private val mimeTypeMap = MimeTypeMap.getSingleton()
    private lateinit var tflite: Interpreter
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var probabilityProcessor: TensorProcessor
    private lateinit var outputProbabilityBuffer: TensorBuffer
    private lateinit var labels:List<String>

    // 결과
    private lateinit var result:String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInstance()

        progressDialog.show()
        setInit()
    }

    private fun setInstance() {
        // mainActivity 관련 객체
        mainActivity =  (requireActivity() as MainActivity)
        contentResolver = mainActivity.contentResolver
        mainViewModels = mainActivity.mainViewModels

        // 로딩바
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage("Please wait.....")

        // Firebase 관련 객체
        storageReference = FirebaseStorage.getInstance().getReference("Animals")

        // Android Binding 객체
        imageView = binding.fragmentAiImage
        imageView.setImageURI(mainViewModels.uploadedImageUri)
        tvEmotion = binding.fragmentAiResultEmotion

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
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
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

        // Firebase Storage Child를 만들고 사용
        val storageReferenceChild = storageReference!!.child(System.currentTimeMillis().toString() + "." + GetFileExtension(mainViewModels.uploadedImageUri))

        storageReferenceChild.putFile(mainViewModels.uploadedImageUri!!)
            .addOnSuccessListener {
                storageReferenceChild.downloadUrl
                    .addOnSuccessListener {
                        val imageShape = tflite.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}
                        imageSizeY = imageShape[1]
                        imageSizeX = imageShape[2]
                        val imageDataType: DataType = tflite.getInputTensor(imageTensorIndex).dataType()
                        val probabilityTensorIndex = 0
                        val probabilityShape: IntArray = tflite.getOutputTensor(probabilityTensorIndex).shape() // {1, NUM_CLASSES}
                        val probabilityDataType: DataType = tflite.getOutputTensor(probabilityTensorIndex).dataType()

                        inputImageBuffer = TensorImage(imageDataType)
                        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
                        probabilityProcessor = TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build()

                        inputImageBuffer = loadImage(bitmap)
                        tflite.run(inputImageBuffer.buffer,TensorBuffer.createFixedSize(probabilityShape, probabilityDataType).buffer.rewind())

                        // 결과 출력
                        val labeledProbability: Map<String, Float> = TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer)).mapWithFloatValue
                        val maxValueInMap = Collections.max(labeledProbability.values)

                        labeledProbability.entries.forEach { entry ->
                            if (entry.value == maxValueInMap) {
                                result = entry.key
                                tvEmotion.text = result
                                Log.d("SSAFY", entry.toString())
                                Log.d("SSAFY", maxValueInMap.toString())
                                Log.d("SSAFY", (entry.value == maxValueInMap).toString() + "\n")
                            }
                        }

                        progressDialog.dismiss()
                    }
            }
    }



    @Throws(IOException::class)
    private fun loadmodelfile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = if(mainViewModel.aiType == 0 ) activity.assets.openFd("model.tflite") else activity.assets.openFd("model2.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startoffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
    }

    fun GetFileExtension(uri: Uri?): String? {
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun getPreprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    }

    private fun getPostprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(PROBABILITY_MEAN,PROBABILITY_STD)
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = min(bitmap.width, bitmap.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(getPreprocessNormalizeOp())
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    companion object {
        // TensorFlow 관련 Final 값
        private const val IMAGE_MEAN = 0.0f
        private const val IMAGE_STD = 1.0f
        private const val PROBABILITY_MEAN = 0.0f
        private const val PROBABILITY_STD = 255.0f
    }
}