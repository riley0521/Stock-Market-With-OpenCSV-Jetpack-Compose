package com.rpfcoding.stockmarketwithopencsv.domain.repository

import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyListing
import com.rpfcoding.stockmarketwithopencsv.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>
}