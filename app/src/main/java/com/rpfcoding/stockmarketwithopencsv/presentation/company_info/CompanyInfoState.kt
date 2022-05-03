package com.rpfcoding.stockmarketwithopencsv.presentation.company_info

import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyInfo
import com.rpfcoding.stockmarketwithopencsv.domain.model.IntraDayInfo

data class CompanyInfoState(
    val intraDayList: List<IntraDayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
