package com.rpfcoding.stockmarketwithopencsv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_company_listings")
data class CompanyListingEntity(
    val symbol: String,
    val name: String,
    val exchange: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)