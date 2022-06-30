package com.ssafy.ccd.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.ssafy.ccd.R

class LoadingDialog(context: Context) : Dialog(context) {
    init {
        // 다이얼 로그 제목을 안보이게...
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loading)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
