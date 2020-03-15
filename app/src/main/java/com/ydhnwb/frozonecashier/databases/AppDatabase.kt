package com.ydhnwb.frozonecashier.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ydhnwb.frozonecashier.models.LocalOrder

@Database(entities = arrayOf(LocalOrder::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localOrderDao(): LocalOrderDao
}