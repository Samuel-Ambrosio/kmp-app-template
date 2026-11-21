package com.samuelav.data.sourcesImpl.local.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Instant

internal class Converters {
    @TypeConverter
    internal fun fromTimestamp(value: Long?): LocalDateTime? =
        value?.let { Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.currentSystemDefault()) }

    @TypeConverter
    internal fun localDateTimeToTimestamp(date: LocalDateTime?): Long? =
        date?.toInstant(TimeZone.currentSystemDefault())?.epochSeconds

    @TypeConverter
    internal fun longListToJson(list: List<Long>?): String = Json.encodeToString(list)

    @TypeConverter
    internal fun jsonToLongList(jsonList: String): List<Long>? = Json.decodeFromString(jsonList)

    @TypeConverter
    internal fun stringListToJson(list: List<String>?): String = Json.encodeToString(list)

    @TypeConverter
    internal fun jsonToStringList(jsonList: String): List<String>? = Json.decodeFromString(jsonList)
}
