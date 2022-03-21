package com.ssafy.ccd.src.dto

import android.net.Uri
import retrofit2.http.Url

data class YoutubeInfo(
    var id:String,
    var imageUrl:String,
    var title:String,
    var date:String,
    var channel:String
)