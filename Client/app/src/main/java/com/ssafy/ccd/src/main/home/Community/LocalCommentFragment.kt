package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalCommentBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates


class LocalCommentFragment : BaseFragment<FragmentLocalCommentBinding>(FragmentLocalCommentBinding::bind, R.layout.fragment_local_comment) {
    private lateinit var mainActivity : MainActivity

    private var postId by Delegates.notNull<Int>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            postId = getInt("postId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            mainViewModel.getCommentList(postId)
        }


    }

}