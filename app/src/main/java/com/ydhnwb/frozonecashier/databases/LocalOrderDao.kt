package com.ydhnwb.frozonecashier.databases

import androidx.room.*
import com.ydhnwb.frozonecashier.models.LocalOrder

@Dao
interface LocalOrderDao {
    @Query("SELECT * FROM LocalOrder")
    fun getAllLocalOrder(): List<LocalOrder>

    @Query("SELECT * FROM LocalOrder WHERE id = :id LIMIT 1")
    fun findById(id: Int): LocalOrder

    @Query("SELECT * FROM LocalOrder WHERE generatedId = :generatedId LIMIT 1")
    fun findByGeneratedId(generatedId: String): LocalOrder

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(orderInJson: LocalOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ordersInJson : List<LocalOrder>)

    @Delete
    fun delete(localOrder: LocalOrder)

    @Query("DELETE FROM LocalOrder WHERE generatedId=:generatedId")
    fun deleteByGeneratedId(generatedId : String)

}