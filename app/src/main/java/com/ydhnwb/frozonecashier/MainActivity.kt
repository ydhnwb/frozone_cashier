package com.ydhnwb.frozonecashier

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.frozonecashier.adapters.OrderAdapter
import com.ydhnwb.frozonecashier.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var orderViewModel: OrderViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupComponent()
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderViewModel.bindPusher()
        orderViewModel.listenToOrders().observe(this, Observer {
            rv_order.adapter?.let { a->
                if(a is OrderAdapter){
                    a.updateRecords(it)
                }
            }
        })
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
    private fun toast(message : String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


