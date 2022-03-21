package com.ssafy.ccd.src.main.information

import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.youtube.player.YouTubeBaseActivity
import com.ssafy.ccd.databinding.ActivityInformationBinding
import com.ssafy.ccd.src.dto.YoutubeInfo
import com.ssafy.ccd.src.main.information.adapter.DogTrainingRecyclerViewAdapter
import kotlinx.coroutines.*

/**
 * @author Jueun
 * 강아지 훈련 정보를 보여준다.
 * Youtube API 를 사용하여 강형욱 동영상 제공
 */
class InformationActivity : YouTubeBaseActivity() {

    lateinit var binding:ActivityInformationBinding

    private lateinit var backBtn:ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var list: MutableList<YoutubeInfo>
    lateinit var youtubeAsyncTask: YoutubeAsyncTask
    lateinit var recyclerAdapter : DogTrainingRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            setInstance()
            setListener()
            setRecyclerview()
        }

        updateDatas()
    }

    private fun setRecyclerview() {
        recyclerAdapter = DogTrainingRecyclerViewAdapter(this, list)
        recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun updateDatas(){
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.Default).async {
                recyclerAdapter.setItem(youtubeAsyncTask.onCallYoutubeChannel())
            }
        }
    }

    private fun setListener() {
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setInstance() {
        recyclerView = binding.activityDogTrainingRv
        backBtn = binding.activityDogTrainingIvBack

        youtubeAsyncTask = YoutubeAsyncTask()
        list = mutableListOf()
    }

    fun showDialog(url:String){

    }
}