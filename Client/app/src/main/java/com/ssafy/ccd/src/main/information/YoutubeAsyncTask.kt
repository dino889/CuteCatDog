package com.ssafy.ccd.src.main.information

import android.app.Application
import android.util.Log
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Thumbnail
import com.ssafy.ccd.src.dto.YoutubeInfo


class YoutubeAsyncTask {

    private val API_KEY = "AIzaSyD1zwMxfCz_qzaWQTxAmip2Q7_BT9JbPeM"
    private val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
    private val JSON_FACTORY: JsonFactory = GsonFactory()
    private val NUMBER_OF_VIDEOS_RETURNED: Long = 10

    var CHANNEL_ID : String = "UCee1MvXr6E8qC_d2WEYTU5g"

    fun onCallYoutubeChannel(): MutableList<YoutubeInfo> {
        try {
            val youtube = YouTube.Builder(
                HTTP_TRANSPORT, JSON_FACTORY
            ) { }.setApplicationName("youtube-search-sample").build()
            val search = youtube.search().list("id,snippet")
            val searchResultList = search.setKey(API_KEY)
                .setChannelId(CHANNEL_ID)
                .setOrder("date")
                .setType("video")
                .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet)")
                .setMaxResults(NUMBER_OF_VIDEOS_RETURNED)
                .execute()
                .items

            if (searchResultList != null) {
                return getResult(searchResultList.iterator())
            }
        } catch (e: Throwable) {
            Log.e("Exception Occurred : ", e.toString())
        }
        return mutableListOf()
    }

    private fun getResult(iteratorSearchResults: Iterator<SearchResult>): MutableList<YoutubeInfo> {
        if (!iteratorSearchResults.hasNext()) {
            println(" There aren't any results for your query.")
        }

        val sb = StringBuilder()
        val dbIn = mutableListOf<YoutubeInfo>()

        while (iteratorSearchResults.hasNext()) {
            val singleVideo = iteratorSearchResults.next()
            val rId = singleVideo.id

            val thumbnail = singleVideo.snippet.thumbnails["default"] as Thumbnail?
            sb.append("ID : " + rId.videoId + " , 제목 : " + singleVideo.snippet.title + " , 썸네일 주소 : " + thumbnail!!.url)
            sb.append("\n")

            dbIn.add(YoutubeInfo(rId.videoId, thumbnail.url, singleVideo.snippet.title, "" , singleVideo.snippet.channelTitle))
        }

        return dbIn
    }
}