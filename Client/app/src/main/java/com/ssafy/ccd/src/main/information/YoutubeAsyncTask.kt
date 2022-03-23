package com.ssafy.ccd.src.main.information

import android.util.Log
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Thumbnail
import com.ssafy.ccd.src.dto.YoutubeInfo


val API_KEY = arrayOf("AIzaSyAPfNi6gjbYYrURYw26_oSqZBsQFRmTC68",
    "AIzaSyD1zwMxfCz_qzaWQTxAmip2Q7_BT9JbPeM",
    "AIzaSyBCY5tbaYnN_lZhHsU6SAcPz3oUwWucubk",
    "AIzaSyDuK7UJUBJIcwXqvwwajVUFYH6OBG8LS4w",
    "AIzaSyCpFl2v1XuP8N2xGIGmM_tpJW-hG1xySQg",
    "AIzaSyCFZYevKJMVD1OpZaRIyl1lcnmWOdOwiZM",
    )
private var HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
private var JSON_FACTORY: JsonFactory = GsonFactory()
private var NUMBER_OF_VIDEOS_RETURNED: Long = 10
private var CHANNEL_ID = arrayListOf("UCee1MvXr6E8qC_d2WEYTU5g", "", "", "") // idx 0 - 강혁욱
private var QUERY_MESSAGE = arrayOf("", "", "고양이 훈련", "")
private var PLAY_LIST = arrayOf("PLVh3TM0B0WtkYkWLww1YnMcoar7kCNHbX", "PLGlUAGFRda9fn-aG7rjKop1JRvYXUObvb", "PLaN3xBtVMok93qLS0RQxn582o9QHuViMB", "PLGlUAGFRda9fA42d4BEC6aKENzo7iZ53d")

class YoutubeAsyncTask {
    var apiCnt = 0

    fun onCallYoutubeChannel(type:Int, pageToken:String): MutableMap<String, Any>? {
        try {
            val youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY){}.setApplicationName("CuteCatDog").build()

            if(PLAY_LIST[type] == ""){
                val search = youtube.search().list("id,snippet")
                val searchMachine = search.setKey(API_KEY[apiCnt])
                    .setQ(QUERY_MESSAGE[type])
                    .setChannelId(CHANNEL_ID[type])
                    .setOrder("date")
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet), nextPageToken, prevPageToken")
                    .setMaxResults(NUMBER_OF_VIDEOS_RETURNED)

                if(pageToken != "") searchMachine.pageToken = pageToken
                val searchResult = searchMachine.execute()
                val searchResultList = searchResult.items
                if (searchResultList != null) {
                    val m = getResult(searchResultList.iterator())
                    m["token"] = searchResult.nextPageToken
                    return m
                }
            }else {

            }

            val playlist = youtube.PlaylistItems().list("id, snippet")

            val playlistMahcine = playlist.setKey(API_KEY[apiCnt])
                .setPlaylistId(PLAY_LIST[type])
                .setMaxResults(NUMBER_OF_VIDEOS_RETURNED)

            if(pageToken != "") playlistMahcine.pageToken = pageToken
            val searchResult = playlistMahcine.execute()
            val searchResultList = searchResult.items
            Log.d("SSAFY-",searchResult.items.toString())
            if (searchResultList != null) {
                val m = getResult2(searchResultList.iterator())
                m["token"] = searchResult.nextPageToken
                return m
            }
        } catch (e: Throwable) {
            Log.e("Exception Occurred : ", e.toString())
        }
        return null
    }

    private fun getResult(iteratorSearchResults: Iterator<SearchResult>): MutableMap<String, Any> {
        if (!iteratorSearchResults.hasNext()) {
            println(" There aren't any results for your query.")
        }

        val sb = StringBuilder()
        val dbIn = mutableMapOf<String, Any>()

        val listDb = mutableListOf<YoutubeInfo>()

        while (iteratorSearchResults.hasNext()) {
            val singleVideo = iteratorSearchResults.next()
            val rId = singleVideo.id

            val thumbnail = singleVideo.snippet.thumbnails["default"] as Thumbnail?
            sb.append("ID : " + rId.videoId + " , 제목 : " + singleVideo.snippet.title + " , 썸네일 주소 : " + thumbnail!!.url)
            sb.append("\n")

            listDb.add(YoutubeInfo(rId.videoId, thumbnail.url, singleVideo.snippet.title, singleVideo.snippet.publishedAt.toString() , singleVideo.snippet.channelTitle))
        }

        dbIn["list"] = listDb
        return dbIn
    }

    private fun getResult2(iteratorSearchResults: Iterator<PlaylistItem>): MutableMap<String, Any> {
        if (!iteratorSearchResults.hasNext()) {
            println(" There aren't any results for your query.")
        }

        val sb = StringBuilder()
        val dbIn = mutableMapOf<String, Any>()
        val listDb = mutableListOf<YoutubeInfo>()

        while (iteratorSearchResults.hasNext()) {
            val singleVideo = iteratorSearchResults.next()
            val rId = singleVideo.snippet.resourceId
            val thumbnail = singleVideo.snippet.thumbnails["default"] as Thumbnail? ?: continue

            sb.append("ID : " + rId.videoId)
            sb.append(" , 제목 : " + singleVideo.snippet.title)
            sb.append(" , 썸네일 주소 : " + thumbnail!!.url)
            sb.append("\n")

            listDb.add(YoutubeInfo(rId.videoId, thumbnail.url, singleVideo.snippet.title, singleVideo.snippet.publishedAt.toString() , singleVideo.snippet.channelTitle))
        }

        dbIn["list"] = listDb
        Log.d("SSAFY-", sb.toString())
        return dbIn
    }
}