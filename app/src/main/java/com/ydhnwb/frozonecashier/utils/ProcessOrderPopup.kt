package com.ydhnwb.frozonecashier.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.Gson
import com.ydhnwb.frozonecashier.R
import com.ydhnwb.frozonecashier.adapters.DetailOrderAdapter
import com.ydhnwb.frozonecashier.databases.AppDatabase
import com.ydhnwb.frozonecashier.models.LocalOrder
import com.ydhnwb.frozonecashier.models.Order
import com.ydhnwb.frozonecashier.models.Product
import com.ydhnwb.frozonecashier.viewmodels.OrderState
import com.ydhnwb.frozonecashier.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.poppup_process_order.view.*

class ProcessOrderPopup : DialogFragment(){
    companion object {
        fun instance(order : Order) : ProcessOrderPopup {
            val args = Bundle()
            args.putParcelable("order", order)
            return ProcessOrderPopup().apply {
                arguments = args
            }
        }
    }

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var db : AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.poppup_process_order,container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Room.databaseBuilder(activity!!.applicationContext, AppDatabase::class.java, "justice_cashier").allowMainThreadQueries().build()
        view.process_order_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter =  DetailOrderAdapter(mutableListOf(), activity!!)
        }
        val o : Order = arguments?.getParcelable("order")!!
        val order = Order().apply {
            generatedId = o.generatedId
            name = o.name
            branch = o.branch
            products = o.products
        }
        view.process_order_rv.adapter = order.products.let { DetailOrderAdapter(it, activity!!) }
        orderViewModel = ViewModelProvider(activity!!).get(OrderViewModel::class.java)
        orderViewModel.listenState().observe(viewLifecycleOwner, Observer {
            when(it){
                is OrderState.IsLoading -> {
                    if(it.state){
                        view.process_order_process.isEnabled = false
                        view.process_order_cancel.isEnabled = false
                        view.process_order_et_buyername.isEnabled = false
                        view.loading.visibility = View.VISIBLE
                    }else{
                        view.process_order_process.isEnabled = true
                        view.process_order_cancel.isEnabled = true
                        view.process_order_et_buyername.isEnabled = true
                        view.loading.visibility = View.GONE
                    }
                }
                is OrderState.FailedCreateOrder -> this.dismiss()
                is OrderState.SuccessCreateOrder -> this.dismiss()
            }
        })
        view.process_order_cancel.setOnClickListener {
            delete(order.generatedId.toString())
            this.dismiss()
        }
        view.process_order_process.setOnClickListener {
            view.process_order_in_buyername.error = null
            val buyerName = view.process_order_et_buyername.text.toString().trim()
            if(buyerName.isNotEmpty()){
                order.name = buyerName
                orderViewModel.processOrder(order)
                view.process_order_in_buyername.error = null
                toast(resources.getString(R.string.info_wait))
            }else{
                view.process_order_in_buyername.error = resources.getString(R.string.info_name_cannot_be_blank)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun delete(generatedId: String){
        db.localOrderDao().deleteByGeneratedId(generatedId)
        val localOrders : MutableList<LocalOrder> = db.localOrderDao().getAllLocalOrder() as MutableList<LocalOrder>
        val convertedOrder = mutableListOf<Order>()
        for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, Order::class.java)) }
        orderViewModel.updateOrderValue(convertedOrder)
    }

    private fun toast(message : String) = Toast.makeText(activity, message,Toast.LENGTH_LONG).show()
}