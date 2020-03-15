package com.ydhnwb.frozonecashier.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ydhnwb.frozonecashier.models.LocalOrder

@Dao
interface LocalOrderDao {
    @Query("SELECT * FROM LocalOrder")
    fun getAllLocalOrder(): List<LocalOrder>

    @Query("SELECT * FROM LocalOrder WHERE id = :id LIMIT 1")
    fun findById(id: Int): LocalOrder

    @Insert
    fun insert(orderInJson: LocalOrder)

    @Delete
    fun delete(localOrder: LocalOrder)

}