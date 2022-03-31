package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.History
import com.ssafy.ccd.util.RetrofitUtil

class HistoryService {

    suspend fun selectAllHistory(userId: Int) = RetrofitUtil.historyService.selectAllHistory(userId)

    suspend fun insetHistory(historyRequestDto: History) = RetrofitUtil.historyService.insertHistory(historyRequestDto)

    suspend fun deleteHistory(historyId: Int) = RetrofitUtil.historyService.deleteHistory(historyId)

    suspend fun selectHistory(id: Int) = RetrofitUtil.historyService.selectHistory(id)
}