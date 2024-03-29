package com.gasparaiciukas.owntrainer.utils.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diaryEntry",
    indices = [Index(value = ["year", "dayOfYear"], unique = true)]
)
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) var diaryEntryId: Int = 0,
    @ColumnInfo(name = "year") var year: Int,
    @ColumnInfo(name = "dayOfYear") var dayOfYear: Int,
    @ColumnInfo(name = "dayOfWeek") var dayOfWeek: Int,
    @ColumnInfo(name = "monthOfYear") var monthOfYear: Int,
    @ColumnInfo(name = "dayOfMonth") var dayOfMonth: Int
)
