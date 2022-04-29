package com.rpfcoding.stockmarketwithopencsv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.rpfcoding.stockmarketwithopencsv.data.local.CompanyListingEntity

@Dao
interface StockDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )

    @Query("DELETE FROM tbl_company_listings")
    suspend fun clearCompanyListings()

    @Query(
        """
        SELECT *
        FROM tbl_company_listings
        WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
        UPPER(:query) == symbol
    """
    )
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>
}