package com.rpfcoding.stockmarketwithopencsv.data.repository

import com.rpfcoding.stockmarketwithopencsv.data.local.StockDatabase
import com.rpfcoding.stockmarketwithopencsv.data.mapper.toCompanyListing
import com.rpfcoding.stockmarketwithopencsv.data.remote.StockApi
import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyListing
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
    private val db: StockDatabase
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

            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(isLoading = false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Could not load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Could not load data"))
            }
        }
    }
}