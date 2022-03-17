package com.ssafy.ccd.src.dto

data class Message (
    val success:Boolean,
    val message:String,
    val data: HashMap<String, Any>,
) {

    constructor() : this(false, "", hashMapOf())
}