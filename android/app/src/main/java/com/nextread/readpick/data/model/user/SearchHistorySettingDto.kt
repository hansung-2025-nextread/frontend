package com.nextread.readpick.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class SearchHistorySettingRequest(
    val enabled: Boolean
)

@Serializable
data class SearchHistorySettingResponse(
    val searchHistoryEnabled: Boolean
)
