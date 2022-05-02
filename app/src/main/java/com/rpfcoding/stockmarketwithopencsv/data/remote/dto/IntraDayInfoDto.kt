package com.rpfcoding.stockmarketwithopencsv.data.remote.dto

import com.squareup.moshi.Json

data class IntraDayInfoDto(
    val timestamp: String,
    val close: Double
)
