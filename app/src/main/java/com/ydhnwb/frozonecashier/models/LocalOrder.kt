package com.ydhnwb.frozonecashier.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = arrayOf(Index(value = ["generatedId"], unique = true)))
data class LocalOrder (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "generatedId") val generatedId : String,
    @ColumnInfo(name = "order_in_json") val orderInJson: String
)