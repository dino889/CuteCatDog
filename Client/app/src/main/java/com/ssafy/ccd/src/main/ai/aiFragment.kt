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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAiBinding
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

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


    // TensorFlow 관련 Final 값
    private val IMAGE_MEAN = 0.0f
    private val IMAGE_STD = 1.0f
    private val PROBABILITY_MEAN = 0.0f
    private val PROBABILITY_STD = 255.0f
    private var imageSizeX = 0
    private var imageSizeY = 0
    private val imageTensorIndex = 0

    // TnesorFlow 관련 객체
    val mimeTypeMap = MimeTypeMap.getSingleton()
    var tflite: Interpreter? = null
    private var inputImageBuffer: TensorImage? = null
    private var probabilityProcessor: TensorProcessor? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog.show()

        setInstance()
        setInit()

        progressDialog.dismiss()
    }

    private fun setInit() {
        // ErrorCheck (객체 확인)
        if(storageReference == null) {
            Log.e("ERROR", "Firebase에서 문제가 발생하였습니다.")
            showCustomToast("Firebase에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

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
                        val imageShape = tflite!!.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}
                        imageSizeY = imageShape[1]
                        imageSizeX = imageShape[2]
                        val imageDataType: DataType = tflite!!.getInputTensor(imageTensorIndex).dataType()
                        val probabilityTensorIndex = 0
                        val probabilityShape: IntArray = tflite!!.getOutputTensor(probabilityTensorIndex).shape() // {1, NUM_CLASSES}
                        val probabilityDataType: DataType = tflite!!.getOutputTensor(probabilityTensorIndex).dataType()
                        inputImageBuffer = TensorImage(imageDataType)
                        probabilityProcessor = TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build()
                        inputImageBuffer = loadImage(mainViewModels.uploadedImage)
                        tflite!!.run(
                            inputImageBuffer!!.buffer,
                            TensorBuffer.createFixedSize(probabilityShape, probabilityDataType).buffer.rewind()
                        )
//                        showresult()
                    }
            }
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
        
        // TensorFlow 객체
        try {
            tflite = Interpreter(loadmodelfile(mainActivity))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun loadmodelfile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startoffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
    }

    fun GetFileExtension(uri: Uri?): String? {
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun getPreprocessNormalizeOp(): TensorOperator? {
        return NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    }

    private fun getPostprocessNormalizeOp(): TensorOperator? {
        return NormalizeOp(PROBABILITY_MEAN,PROBABILITY_STD)
    }

    private fun loadImage(bitmap: Bitmap): TensorImage? {
        // Loads bitmap into a TensorImage.
        inputImageBuffer!!.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = Math.min(bitmap.width, bitmap.height)
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(getPreprocessNormalizeOp())
            .build()
        return imageProcessor.process(inputImageBuffer)
    }
}