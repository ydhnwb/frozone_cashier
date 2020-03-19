package com.ydhnwb.frozonecashier

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.ydhnwb.frozonecashier.adapters.OrderAdapter
import com.ydhnwb.frozonecashier.databases.AppDatabase
import com.ydhnwb.frozonecashier.models.LocalOrder
import com.ydhnwb.frozonecashier.models.Order
import com.ydhnwb.frozonecashier.utils.JusticeUtils
import com.ydhnwb.frozonecashier.viewmodels.OrderState
import com.ydhnwb.frozonecashier.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.etc_no_branch.*
import kotlinx.android.synthetic.main.etc_waiting_for_transaction.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var orderViewModel: OrderViewModel
    private var selectedBranch : Int = 0
    private lateinit var db : AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupComponent()
        Thread(Runnable {
            if (JusticeUtils.isFirstTime(this@MainActivity)) {
                runOnUiThread { startActivity(Intent(this@MainActivity, IntroActivity::class.java).also { finish() })}
            }
        }).start()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "justice_cashier").allowMainThreadQueries().build()
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        fetchFromLocal()

        orderViewModel.listenToPivotOrder().observe(this, Observer {
            handleDataFlow(it)
        })
        orderViewModel.listenState().observe(this, Observer {
            handleState(it)
        })

        orderViewModel.listenToOrders().observe(this, Observer {
            rv_order.adapter?.let { a->
                if(JusticeUtils.getCurrentBranch(this@MainActivity) == 0){
                    noBranchViewVisbility(true)
                    emptyViewVisibility(false)
                }else{
                    if(a is OrderAdapter){
                        if(it.isEmpty()){
                            a.updateRecords(it)
                            emptyViewVisibility(true)
                            noBranchViewVisbility(false)
                        }else{
                            a.updateRecords(it)
                            emptyViewVisibility(false)
                            noBranchViewVisbility(false)
                        }
                    }
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
            R.id.action_settings ->{
                startActivity(Intent(this@MainActivity, PromptPinActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupComponent(){
        rv_order.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            adapter = OrderAdapter(mutableListOf(),this@MainActivity)
        }
    }
    private fun toast(message : String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun showInfoAlert(message : String){
        val alertDialog = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if(JusticeUtils.getCurrentBranch(this@MainActivity) == 0 && !JusticeUtils.isFirstTime(this)){
            showInfoAlert(resources.getString(R.string.info_no_branch_selected))
            noBranchViewVisbility(true)
            emptyViewVisibility(false)
        }else{
            selectedBranch = JusticeUtils.getCurrentBranch(this)
            if(selectedBranch != 0){
                val i = JusticeUtils.getCurrentBranch(this@MainActivity)
                val t = selectedBranch != i
                orderViewModel.bindPusher(i.toString(), t)
            }
        }
    }

    private fun noBranchViewVisbility(state : Boolean){
        if(state){ etc_no_branch_view.visibility = View.VISIBLE }else{ etc_no_branch_view.visibility = View.GONE }
    }

    private fun emptyViewVisibility(state : Boolean){
        if(state){
            etc_waiting_for_transaction_view.visibility = View.VISIBLE
        }else{
            etc_waiting_for_transaction_view.visibility = View.GONE
        }
    }

    private fun handleState(it: OrderState){
        when(it){
            is OrderState.SuccessCreateOrder -> {
                delete(it.generatedId)
                toast(resources.getString(R.string.info_success_order))
            }
            is OrderState.FailedCreateOrder -> showAlert(resources.getString(R.string.info_cannot_order))
            is OrderState.ShowToast -> toast(it.message)
            is OrderState.AttachToRecycler -> {}
            is OrderState.ClearLocalDatabase -> { db.clearAllTables() }
        }
    }

    private fun handleDataFlow(order : Order){
        val orderInJson = Gson().toJson(order)
        db.localOrderDao().insert(LocalOrder(orderInJson =  orderInJson, generatedId = order.generatedId.toString()))
        val localOrders : List<LocalOrder> = db.localOrderDao().getAllLocalOrder()
        val convertedOrder = mutableListOf<Order>()
        for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, Order::class.java)) }
        orderViewModel.updateOrderValue(convertedOrder)
    }

    private fun fetchFromLocal(){
        val localOrders : List<LocalOrder> = db.localOrderDao().getAllLocalOrder()
        val convertedOrder = mutableListOf<Order>()
        for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, Order::class.java)) }
        orderViewModel.updateOrderValue(convertedOrder)
    }

    private fun delete(generatedId: String){
        db.localOrderDao().deleteByGeneratedId(generatedId)
        val localOrders : MutableList<LocalOrder> = db.localOrderDao().getAllLocalOrder() as MutableList<LocalOrder>
        val convertedOrder = mutableListOf<Order>()
        for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, Order::class.java)) }
        orderViewModel.updateOrderValue(convertedOrder)
    }

    private fun showAlert(message : String){
        val alertDialog = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }
        alertDialog.show()
    }
}