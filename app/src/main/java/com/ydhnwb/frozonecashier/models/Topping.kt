package com.ydhnwb.frozonecashier.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topping(
    @SerializedName("id")
    var id : Int? = null,
    @SerializedName("name")
    var name : String? = null,
    @SerializedName("category")
    var category : String? = null,
    @SerializedName("price")
    var price : Int? = 0,
    var isChecked : Boolean = false
) : Parcelable