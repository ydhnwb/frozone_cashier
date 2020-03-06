package com.ydhnwb.frozonecashier.viewmodels

import androidx.lifecycle.ViewModel
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.ydhnwb.frozonecashier.models.Order
import com.ydhnwb.frozonecashier.utils.SingleLiveEvent

class OrderViewModel : ViewModel(){
    private val pusher : Pusher = Pusher("af150b12c7f8cf0e920f", PusherOptions().apply { setCluster("ap1") })
    private val channel: Channel = pusher.subscribe("product-order")
    private var state : SingleLiveEvent<OrderState> = SingleLiveEvent()
    private lateinit var eventListener: SubscriptionEventListener

    init {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {
                println("State changed from " + change?.getPreviousState() + " to " + change?.getCurrentState());
            }
            override fun onError(message: String?, code: String?, e: Exception?) {
                println("Pusher error: $message with code $code because of ${e?.message}")
            }
        }, ConnectionState.ALL)
    }

    fun bindPusher(){


    }

    fun unbindPusher(){
        channel.unbind("App\\Events\\Order", eventListener)
    }

    fun listenState() = state


}

sealed class OrderState {
    data class AttachToRecycler(var e : String) : OrderState()
    data class ShowToast(var message : String) :OrderState()
}