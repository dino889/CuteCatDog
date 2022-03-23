package com.ssafy.ccd.src.main.information

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ssafy.ccd.R
import com.ssafy.ccd.src.main.information.util.FullscreenableChromeClient

lateinit var wvYoutube : WebView

class YoutubeDialog(val context:Context, val activity: Activity) {

    @SuppressLint("SetJavaScriptEnabled")
    fun callDialog(id:String){
        val dlg = Dialog(context)
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_youtube)

        wvYoutube = dlg.findViewById(R.id.dialogYoutube_wb)

        wvYoutube.webChromeClient = FullscreenableChromeClient(activity, dlg)
        wvYoutube.webViewClient = WebViewClient()

        wvYoutube.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            pluginState = WebSettings.PluginState.ON
            setAppCacheEnabled(true)
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        wvYoutube.loadUrl("https://www.youtube.com/embed/$id")

        dlg.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(p0: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
                    dlg.dismiss()
                    return true
                }
                return false
            }
        })

        dlg.show()
    }
}