package com.ydhnwb.frozonecashier.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalOrder (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "order_in_json") val orderInJson: String
)