package com.ydhnwb.frozonecashier.databases

import androidx.room.*
import com.ydhnwb.frozonecashier.models.LocalOrder

@Dao
interface LocalOrderDao {
    @Query("SELECT * FROM LocalOrder")
    fun getAllLocalOrder(): List<LocalOrder>

    @Query("SELECT * FROM LocalOrder WHERE id = :id LIMIT 1")
    fun findById(id: Int): LocalOrder

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(orderInJson: LocalOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ordersInJson : List<LocalOrder>)

    @Delete
    fun delete(localOrder: LocalOrder)

}