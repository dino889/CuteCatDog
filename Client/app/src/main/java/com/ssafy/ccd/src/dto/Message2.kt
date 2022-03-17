package com.ssafy.ccd.src.dto

data class Message2 (
    val success:Boolean,
    val message:String,
    val data: Boolean,
        ){
    constructor() : this(false, "", false)
}