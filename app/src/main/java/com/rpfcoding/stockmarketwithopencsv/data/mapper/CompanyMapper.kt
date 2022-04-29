package com.rpfcoding.stockmarketwithopencsv.data.mapper

import com.rpfcoding.stockmarketwithopencsv.data.local.CompanyListingEntity
import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}