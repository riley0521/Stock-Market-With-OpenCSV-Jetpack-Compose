package com.rpfcoding.stockmarketwithopencsv.di

import com.rpfcoding.stockmarketwithopencsv.data.csv.CSVParser
import com.rpfcoding.stockmarketwithopencsv.data.csv.CompanyListingsParser
import com.rpfcoding.stockmarketwithopencsv.data.repository.StockRepositoryImpl
import com.rpfcoding.stockmarketwithopencsv.domain.model.CompanyListing
import com.rpfcoding.stockmarketwithopencsv.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(companyListingsParser: CompanyListingsParser): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(stockRepositoryImpl: StockRepositoryImpl): StockRepository
}