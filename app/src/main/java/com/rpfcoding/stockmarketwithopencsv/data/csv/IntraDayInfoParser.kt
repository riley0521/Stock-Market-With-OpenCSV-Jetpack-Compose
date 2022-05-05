package com.rpfcoding.stockmarketwithopencsv.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.rpfcoding.stockmarketwithopencsv.data.mapper.toIntraDayInfo
import com.rpfcoding.stockmarketwithopencsv.data.remote.dto.IntraDayInfoDto
import com.rpfcoding.stockmarketwithopencsv.domain.model.IntraDayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("BlockingMethodInNonBlockingContext")
@Singleton
class IntraDayInfoParser @Inject constructor() : CSVParser<IntraDayInfo> {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntraDayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0)
                    val close = line.getOrNull(4)?.toDouble()

                    IntraDayInfoDto(
                        timestamp = timestamp ?: return@mapNotNull null,
                        close = close ?: return@mapNotNull null
                    ).toIntraDayInfo()
                }
                .filter {
                    var daysToSubtract: Long = 1

                    when(LocalDate.now().minusDays(daysToSubtract).dayOfWeek) {
                        DayOfWeek.SUNDAY -> {
                            daysToSubtract += 2
                        }
                        DayOfWeek.SATURDAY -> {
                            daysToSubtract += 1
                        }
                        else -> Unit
                    }

                    it.date.dayOfMonth == LocalDate.now().minusDays(daysToSubtract).dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}