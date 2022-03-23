package com.ssafy.ccd.src.dto


import com.google.firebase.database.*

data class YoutubeInfo(
    var id:String = "",
    var imageUrl:String = "",
    var title:String = "",
    var date:String = ServerValue.TIMESTAMP.toString(),
    var channel:String = "",
    var type:Int = 0,
)