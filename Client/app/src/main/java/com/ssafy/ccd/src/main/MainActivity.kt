package com.ssafy.ccd.src.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageButton
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

        binding.activityMainFabCam.setOnClickListener {
            showAiDialog()
        }
    }
    fun showAiDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_ai_dialog,null)
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogView)
        dialogView.findViewById<ImageButton>(R.id.fragment_ai_dialog_cancle).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}