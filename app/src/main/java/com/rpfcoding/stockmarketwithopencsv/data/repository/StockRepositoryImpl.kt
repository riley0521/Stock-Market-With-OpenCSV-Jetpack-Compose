package com.rpfcoding.stockmarketwithopencsv.data.repository

import com.rpfcoding.stockmarketwithopencsv.data.csv.CSVParser
import com.rpfcoding.stockmarketwithopencsv.data.local.StockDatabase
import com.rpfcoding.stockmarketwithopencsv.data.mapper.toCompanyInfo
import com.rpfcoding.stockmarketwithopencsv.data.mapper.toCompanyListing
import com.rpfcoding.stockmarketwithopencsv.data.mapper.toCompanyListingEntity
import com.rpfcoding.stockmarketwithopencsv.data.remote.StockApi
import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyInfo
import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyListing
import com.rpfcoding.stockmarketwithopencsv.domain.model.IntraDayInfo
import com.rpfcoding.stockmarketwithopencsv.domain.repository.StockRepository
import com.rpfcoding.stockmarketwithopencsv.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intraDayInfoParser: CSVParser<IntraDayInfo>,
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))

            val localListings = dao.searchCompanyListing(query)

            emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(isLoading = false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()

                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Could not load company listings."))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Could not load company listings."))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })

                emit(
                    Resource.Success(
                        data = dao.searchCompanyListing("").map { it.toCompanyListing() })
                )
                emit(Resource.Loading(isLoading = false))
            }
        }
    }

    override suspend fun getIntraDayInfo(symbol: String): Resource<List<IntraDayInfo>> {
        return try {
            val response = api.getIntraDayInfo(symbol)

            val results = intraDayInfoParser.parse(response.byteStream())

            Resource.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load intra day information.")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load intra day information.")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)

            Resource.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load company information.")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load company information.")
        }
    }
}