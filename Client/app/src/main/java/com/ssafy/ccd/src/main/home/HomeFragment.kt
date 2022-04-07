package com.ssafy.ccd.src.main.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentHomeBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.ItemInfo
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.main.home.Information.InformationRecyclerViewAdapter
import com.ssafy.ccd.src.main.information.InformationActivity
import com.ssafy.ccd.src.main.mypage.MyScheduleRecyclerviewAdapter
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "HomeFragment"
//class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
class HomeFragment : Fragment() {
    val mainViewModel: MainViewModels by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    private lateinit var petAdapter:HomeProfilePetsAdapter
    private lateinit var mainActivity : MainActivity
    private lateinit var calendarAdapter : MyScheduleRecyclerviewAdapter

    // Binding items
    private lateinit var ivKnowledge : ImageView
    private lateinit var ivHomeUserImg : ImageView
    private lateinit var rvInformation : RecyclerView
    private lateinit var vpBanner : ViewPager2

    // Information List
    private lateinit var rvAdapterinfo : InformationRecyclerViewAdapter

    // viewPager Banner
    private var list = mutableListOf(R.drawable.banner5, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4)
    private var currentPosition = 0
    private var myHandler = MyHandler()
    private val intervalTime = 1500.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

    // community
    private val BOARD_TYPE_TOWN = 1
    private val BOARD_TYPE_QNA = 2
    private val BOARD_TYPE_SHARE = 3

    private lateinit var locBoardAdapter: BoardAdapter
    private lateinit var qnaBoardAdapter: BoardAdapter
    private lateinit var shareBoardAdapter: BoardAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    /**
     * @author All
     * @since 03/22/22
     * Firebase Store 사용시에
     * BaseFragment를 사용할 수
     * 없어서 해당 부분으로 교체
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInstance()
        setListener()
        initLoc()

        runBlocking {
            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            val now = LocalDate.now()
            val strNow = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            val todayMillisecond = CommonUtils.makeBirthMilliSecond(strNow)
            mainViewModel.getMyPetsAllList(userId)
            mainViewModel.getUserInfo(userId, true)
            mainViewModel.getAllPostList()
            mainViewModel.getAllUserList()
            mainViewModel.getLikePostsByUserId(userId)
            mainViewModel.getCalendarListbyDate(userId, todayMillisecond)
            mainViewModel.userLoc?.let { mainViewModel.getLocPostListByUserLoc(it) }
        }

        mainViewModel.loginUserInfo.observe(viewLifecycleOwner) {
            binding.loginUser = it
        }

        initPostList()
        initAdapter()
        initBanner()

        moveBoardDetailClickEvent()

        ringTheBell()
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 리스너 설정
     */
    private fun setListener() {
        ivHomeUserImg.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.homeFragment)
        }

        ivKnowledge.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.informationMainFragment)
        }

        binding.fragmentHomeNoti.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.notificationFragment)
        }
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 객체 생성
     */
    private fun setInstance() {
        mainActivity = context as MainActivity
        binding.viewModel = mainViewModel

        // binding
        ivHomeUserImg = binding.fragmentHomeUserImg
        ivKnowledge = binding.fragmentHomeIvKnowledge
        rvInformation = binding.fragmentHomeRvInformation
        vpBanner = binding.fragmentHomeVpBanner

        // Information
        rvAdapterinfo = InformationRecyclerViewAdapter(requireContext(), mutableListOf(
            // TODO 추후에 이미지 변경해야함.
            ItemInfo( "dogtraining", Intent(requireActivity(), InformationActivity::class.java).putExtra("type", 0)),
            ItemInfo("dogemotionboard", Intent(requireActivity(), InformationActivity::class.java).putExtra("type", 1)),
            ItemInfo( "cattraining", Intent(requireActivity(), InformationActivity::class.java).putExtra("type", 2)),
            ItemInfo( "catemotion", Intent(requireActivity(), InformationActivity::class.java).putExtra("type", 3)),
        ))
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 어뎁터 초기설정
     */
    private fun initAdapter(){
        // myPetsList Adapter
        mainViewModel.myPetsList.observe(viewLifecycleOwner) {
            petAdapter = HomeProfilePetsAdapter(it)
            binding.fragmentHomeRvPets.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = petAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            petAdapter.setAddClickListener(object : HomeProfilePetsAdapter.AddClickListener {
                override fun onClick(view: View, position: Int) {
                    this@HomeFragment.findNavController().navigate(R.id.action_homeFragment_to_addPetFragment)
                }
            })

            petAdapter.setItemClickListener(object : HomeProfilePetsAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, pet: Pet) {
                    val petId = bundleOf("petId" to pet.id)
                    this@HomeFragment.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment,petId)
                }
            })
        }

        // Information Adapter
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL

        rvInformation.apply {
            layoutManager = manager
            adapter = rvAdapterinfo
        }

        //Calendar Adapter
        mainViewModel.schedule.observe(viewLifecycleOwner){
            Log.d(TAG, "initAdapter: $it")
            calendarAdapter = MyScheduleRecyclerviewAdapter()
            calendarAdapter.list = it
            binding.fragmentHomeRvTodayCalenderNoti.apply {
                layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                adapter = calendarAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart(intervalTime)
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 롤링배너 핸들러
     */
    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                binding.fragmentHomeVpBanner.setCurrentItem(++currentPosition % list.size, true) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 이어서 한다.
            }
        }
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 롤링배너 정지 함수
     */
    private fun autoScrollStop(){
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 롤링배너 초기화 관련 함수
     */
    private fun initBanner() {
        binding.fragmentHomeVpBanner.adapter = BannerAdapter(list)
        binding.fragmentHomeVpBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.fragmentHomeVpBanner.setCurrentItem(currentPosition, true)
        binding.fragmentHomeVpBanner.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        // 뷰페이저 움직이는 중
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }
        binding.wormDotsIndicator.setViewPager2(binding.fragmentHomeVpBanner)
    }

    /**
     * @author Jueun
     * @since 03/22/22
     * 롤링배너 관련 함수
     */
    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0)
        myHandler.sendEmptyMessageDelayed(0, intervalTime)
    }

    /**
     * @author Jiwoo
     * @since 03/23/22
     * 게시판 타입별 데이터 초기화
     */
    private fun initPostList() {
        val localRv = binding.fragmentHomeCommuLocalRv
        val qnaRv = binding.fragmentHomeCommuQnARv
        val shareRv = binding.fragmentHomeCommuShareRv


        localRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        qnaRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        shareRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        mainViewModel.locPostList.observe(viewLifecycleOwner, {
            locBoardAdapter = BoardAdapter(it, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
            localRv.adapter = locBoardAdapter
        })

        qnaBoardAdapter = BoardAdapter(mainViewModel.qnaPostList.value!!, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
        shareBoardAdapter = BoardAdapter(mainViewModel.sharePostList.value!!, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())

        qnaRv.adapter = qnaBoardAdapter
        shareRv.adapter = shareBoardAdapter
    }

    /**
     * @author Jiwoo
     * @since 03/23/22
     * 게시판 이동 버튼 클릭 이벤트
     */
    private fun moveBoardDetailClickEvent() {
        binding.fragmentHomeCommuLocalBtnBack.setOnClickListener {
            mainViewModel.boardId = 0
            this@HomeFragment.findNavController().navigate(R.id.localBoardFragment)
        }

        binding.fragmentHomeCommuQnABtnBack.setOnClickListener {
            mainViewModel.boardId = 0
            this@HomeFragment.findNavController().navigate(R.id.qnaBoardFragment)
        }

        binding.fragmentHomeCommuShareBtnBack.setOnClickListener {
            mainViewModel.boardId = 0
            this@HomeFragment.findNavController().navigate(R.id.shareBoardFragment)
        }
    }

    /**
     * @author Jiwoo
     * @since 03/31/22
     * fcm 수신 시 알림 animation
     */
    private fun ringTheBell() {
        val intentFilter = IntentFilter("com.ssafy.ccd")
        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent!!.action
                binding.fragmentHomeNoti.playAnimation()
            }
        }
        mainActivity.registerReceiver(receiver, intentFilter)
    }

    private fun initLoc() {
        if(mainActivity.checkPermissionForLocation(requireContext())) {
            runBlocking {
                mainActivity.startLocationUpdates()
            }
        }
    }
}