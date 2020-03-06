package com.ydhnwb.frozonecashier.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("branch") var branch : Int? = null,
    @SerializedName("products") var products : MutableList<Product>
) : Parcelable