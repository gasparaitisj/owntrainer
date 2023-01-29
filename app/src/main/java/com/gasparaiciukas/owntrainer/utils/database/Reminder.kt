package com.gasparaiciukas.owntrainer.utils.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = true) var reminderId: Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int
)
