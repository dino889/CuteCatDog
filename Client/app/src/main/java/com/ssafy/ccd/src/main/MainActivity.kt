package com.ssafy.ccd.src.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseActivity
import com.ssafy.ccd.databinding.ActivityMainBinding

class MainActivity :BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding.activityMainBottomNavigationView.background = null
        binding.activityMainBottomNavigationView.menu.getItem(2).isEnabled = false
        // 네비게이션 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_navHost) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바인딩
        NavigationUI.setupWithNavController(binding.activityMainBottomNavigationView, navController)
    }
}