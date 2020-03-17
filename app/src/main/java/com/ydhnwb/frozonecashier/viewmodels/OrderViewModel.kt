package com.ydhnwb.frozonecashier.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.ydhnwb.frozonecashier.models.Order
import com.ydhnwb.frozonecashier.utils.JusticeUtils
import com.ydhnwb.frozonecashier.utils.SingleLiveEvent
import com.ydhnwb.frozonecashier.webservices.JustApi
import com.ydhnwb.frozonecashier.webservices.WrappedResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel : ViewModel(){
    private val pusher : Pusher = Pusher(JusticeUtils.PUSHER_KEY, PusherOptions().apply { setCluster(JusticeUtils.CLUSTER_NAME) })
    private val channel: Channel = pusher.subscribe(JusticeUtils.CHANNEL_NAME)
    private var eventListener: SubscriptionEventListener
    private var state : SingleLiveEvent<OrderState> = SingleLiveEvent()
    private var orders = MutableLiveData<List<Order>>()
    private var hasSubscribed = MutableLiveData<Boolean>().apply { value = false }
    private var pivotOrder = MutableLiveData<Order>()
    private var api = JustApi.instance()

    init {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) { println("State changed from " + change?.getPreviousState() + " to " + change?.getCurrentState()); }
            override fun onError(message: String?, code: String?, e: Exception?) { println("Pusher error: $message with code $code because of ${e?.message}") }
        }, ConnectionState.ALL)

        eventListener = SubscriptionEventListener {
//            val selectedProducts = if(orders.value == null){ mutableListOf() } else { orders.value as MutableList<Order> }
            val jsonObj = JSONObject(it.data)
            val ja_dataArr = jsonObj.getJSONArray("message")
            val order = Gson().fromJson(ja_dataArr.get(0).toString(), Order::class.java)
            order.generatedId = JusticeUtils.getRandomMillis()
            pivotOrder.postValue(order)
        }
    }

    fun bindPusher(){
        hasSubscribed.value = true
        channel.bind(JusticeUtils.EVENT_NAME, eventListener)
    }

    fun deleteOrderAtPosition(position : Int){
        orders.value?.let {
            if(!it.isNullOrEmpty()){
                val reversedOrder : MutableList<Order> = it.reversed().toMutableList()
                reversedOrder.removeAt(position)
                reversedOrder.reverse()
                orders.postValue(reversedOrder)
            }
        } ?: kotlin.run {
            println("The value is null")
        }
    }

    fun processOrder(order: Order){
        try{
            state.value = OrderState.IsLoading(true)
            val gson = Gson().toJson(order)
            println(gson)
            api.createOrder(gson).enqueue(object: Callback<WrappedResponse<Order>>{
                override fun onFailure(call: Call<WrappedResponse<Order>>, t: Throwable) {
                    println("onFailure createOrder -> ${t.message}")
                    state.value = OrderState.IsLoading(false)
                    state.value = OrderState.ShowToast("Gagal saat mengkonfirmasi pesanan")
                    state.value = OrderState.FailedCreateOrder
                }

                override fun onResponse(call: Call<WrappedResponse<Order>>, response: Response<WrappedResponse<Order>>) {
                    if(response.isSuccessful){
                        val b = response.body() as WrappedResponse
                        if(b.status){
                            state.value = OrderState.SuccessCreateOrder(order.generatedId.toString())
                        }else{
                            state.value = OrderState.FailedCreateOrder
                            println(response.body())
                            state.value = OrderState.ShowToast("Gagal membuat pesanan")
                        }
                    }else{
                        println(response.code())
                        println(response.message())
                        state.value = OrderState.FailedCreateOrder
                        state.value = OrderState.ShowToast("Tidak dapat mengonfirmasi pesanan")
                    }
                    state.value = OrderState.IsLoading(false)
                }

            })
        }catch (e: Exception){
            println(e.message)
            state.value = OrderState.IsLoading(false)
            state.value = OrderState.ShowToast("Gagal saat mengkonfirmasi pesanan")
            state.value = OrderState.FailedCreateOrder
        }
    }

    fun updateOrderValue(ors : List<Order>){ orders.postValue(ors) }
    fun unbindPusher(){ channel.unbind(JusticeUtils.EVENT_NAME, eventListener) }
    fun listenState() = state
    fun listenToOrders() = orders
    fun listenToPivotOrder() = pivotOrder


}

sealed class OrderState {
    data class SuccessCreateOrder(var generatedId : String) : OrderState()
    object FailedCreateOrder : OrderState()
    data class IsLoading(var state: Boolean) : OrderState()
    data class AttachToRecycler(var e : String) : OrderState()
    data class ShowToast(var message : String) :OrderState()
    object ClearLocalDatabase : OrderState()
}