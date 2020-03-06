package com.ydhnwb.frozonecashier

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private val pusher : Pusher = Pusher(JusticeUtils.PUSHER_KEY, PusherOptions().apply { setCluster(JusticeUtils.CLUSTER_NAME) })
    private val channel: Channel = pusher.subscribe(JusticeUtils.CHANNEL_NAME)
    private lateinit var eventListener: SubscriptionEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupComponent()
        initPusher()
        bindPusher()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindPusher()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupComponent(){
        rv_order.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = OrderAdapter(mutableListOf(),this@MainActivity)
        }
    }

    private fun initPusher(){
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {
                println("State changed from " + change?.getPreviousState() + " to " + change?.getCurrentState());
            }
            override fun onError(message: String?, code: String?, e: Exception?) {
                println("Pusher error: $message with code $code because of ${e?.message}")
            }
        }, ConnectionState.ALL)

        eventListener = SubscriptionEventListener {
            runOnUiThread {
                println(it.data.toString())
                rv_order.adapter?.let { adapter ->
                    if(adapter is OrderAdapter){
                        val jsonObj = JSONObject(it.data)
                        val ja_data= jsonObj.getJSONObject("message")
                        val order = Gson().fromJson<Order>(ja_data.toString(), Order::class.java)
                        adapter.updateList(order)
                    }
                }
            }
        }
    }

    private fun bindPusher() = channel.bind(JusticeUtils.EVENT_NAME, eventListener)
    private fun unbindPusher() = channel.unbind(JusticeUtils.EVENT_NAME, eventListener)
    private fun toast(message : String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


