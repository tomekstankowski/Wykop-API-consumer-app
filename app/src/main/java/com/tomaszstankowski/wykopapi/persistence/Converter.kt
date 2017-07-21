package com.tomaszstankowski.wykopapi.persistence

import android.arch.persistence.room.TypeConverter
import java.util.*


class Converter {

    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun fromDate(value: Date?): Long? {
        return if (value == null) null else value.time
    }
}