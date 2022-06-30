package com.ssafy.ccd.src.main.information

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.internal.Mutable
import com.google.android.youtube.player.YouTubeBaseActivity
import com.ssafy.ccd.databinding.ActivityInformationBinding
import com.ssafy.ccd.src.dto.YoutubeInfo
import com.ssafy.ccd.src.main.information.adapter.InformationRecyclerViewAdapter
import kotlinx.coroutines.*

/**
 * @author Jueun
 * 훈련 정보를 보여준다.
 * Youtube API 를 사용하여 강형욱 동영상 제공
 */
class InformationActivity : YouTubeBaseActivity() {

    lateinit var binding:ActivityInformationBinding
    lateinit var recyclerView: RecyclerView
    lateinit var list: MutableList<YoutubeInfo>
    lateinit var youtubeAsyncTask: YoutubeAsyncTask
    lateinit var recyclerAdapter : InformationRecyclerViewAdapter
    lateinit var tvTitle : TextView

    private lateinit var backBtn:ImageView
    private var pageToken = ""
    private var type = 0
    private var endTrigger = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            setInstance()
            setInitial()
            setListener()
            setRecyclerview()
        }

        updateDatas()
    }

    private fun setInitial() {
        // 훈련 타입을 가져온다.
        type = intent.getIntExtra("type", 0)

        // 타입에 따라 제목을 수정해준다.
        when(type){
            0 -> tvTitle.text = "강아지 훈련교실"
            1 -> tvTitle.text = "강아지 교감교실"
            2 -> tvTitle.text = "고양이 훈련교실"
            3 -> tvTitle.text = "고양이 교감교실"
        }
    }

    private fun setRecyclerview() {
        recyclerAdapter = InformationRecyclerViewAdapter(this, list)
        recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    fun updateDatas(){
        GlobalScope.launch {
            val m = youtubeAsyncTask.onCallYoutubeChannel(type, pageToken)
            if(m != null) {
                runOnUiThread{
                    recyclerAdapter.addItems(m["list"] as MutableList<YoutubeInfo>)
                }
                pageToken = m["token"] as String
            }
            else {
                if(youtubeAsyncTask.apiCnt < API_KEY.size) {
                    youtubeAsyncTask.apiCnt++
                    updateDatas()
                }
                else {
                    runOnUiThread {
//                        Toast.makeText(applicationContext, "Youtube API 할당량을 초과하였습니다. 다음 달 서비스로 돌아오겠습니다. 감사합니다.", Toast.LENGTH_SHORT).show()
                        if(!endTrigger){
                            recyclerAdapter.deleteLoading()
                            endTrigger = true
                        }
                    }
                }
            }
        }
    }

    private fun setListener() {
        backBtn.setOnClickListener {
            finish()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1

                // 스크롤이 끝에 도달했는지 확인
                if (lastVisibleItemPosition == itemTotalCount) {
//                    runBlocking {
//                        recyclerAdapter.deleteLoading()
//                    }
                    updateDatas()
                }
            }
        })
    }

    private fun setInstance() {
        recyclerView = binding.activityDogTrainingRv
        backBtn = binding.activityDogTrainingIvBack
        tvTitle = binding.activityDogTrainingTvTitle

        youtubeAsyncTask = YoutubeAsyncTask()
        list = mutableListOf()
    }
}