package com.ydhnwb.frozonecashier.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product (
    @SerializedName("id") var id : Int? = 0,
    @SerializedName("name") var name : String? = null,
    @SerializedName("category") var category : String? = null,
    @SerializedName("price") var price : Int? = 0,
    @SerializedName("image") var image : String?= null,
    @SerializedName("description") var description : String?= null,
    var selectedToppings : MutableList<Topping> = mutableListOf()
): Parcelable