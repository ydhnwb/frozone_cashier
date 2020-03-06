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
import com.ydhnwb.frozonecashier.adapters.OrderAdapter
import com.ydhnwb.frozonecashier.models.Order
import com.ydhnwb.frozonecashier.utils.JusticeUtils
import com.ydhnwb.frozonecashier.utils.SingleLiveEvent
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject

class OrderViewModel : ViewModel(){
    private val pusher : Pusher = Pusher(JusticeUtils.PUSHER_KEY, PusherOptions().apply { setCluster(JusticeUtils.CLUSTER_NAME) })
    private val channel: Channel = pusher.subscribe(JusticeUtils.CHANNEL_NAME)
    private var eventListener: SubscriptionEventListener
    private var state : SingleLiveEvent<OrderState> = SingleLiveEvent()
    private var orders = MutableLiveData<List<Order>>()
    private var hasSubscribed = MutableLiveData<Boolean>().apply { value = false }

    init {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {
                println("State changed from " + change?.getPreviousState() + " to " + change?.getCurrentState());
            }
            override fun onError(message: String?, code: String?, e: Exception?) {
                println("Pusher error: $message with code $code because of ${e?.message}")
            }
        }, ConnectionState.ALL)
        eventListener = SubscriptionEventListener {
            val selectedProducts = if(orders.value == null){
                mutableListOf()
            } else {
                orders.value as MutableList<Order>
            }
            val jsonObj = JSONObject(it.data)
            val ja_dataArr = jsonObj.getJSONArray("message")
            val order = Gson().fromJson<Order>(ja_dataArr.get(0).toString(), Order::class.java)
            selectedProducts.add(order)
            orders.postValue(selectedProducts)
        }
    }

    fun bindPusher(){
        hasSubscribed.value = true
        channel.bind(JusticeUtils.EVENT_NAME, eventListener)
    }

    fun unbindPusher(){ channel.unbind(JusticeUtils.EVENT_NAME, eventListener) }

    fun listenState() = state

    fun listenToOrders() = orders


}

sealed class OrderState {
    data class AttachToRecycler(var e : String) : OrderState()
    data class ShowToast(var message : String) :OrderState()
}