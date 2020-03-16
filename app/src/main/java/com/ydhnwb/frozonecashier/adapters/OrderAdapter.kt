package com.ydhnwb.frozonecashier.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.frozonecashier.R
import com.ydhnwb.frozonecashier.models.Order
import com.ydhnwb.frozonecashier.models.Product
import com.ydhnwb.frozonecashier.utils.ProcessOrderPopup
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.etc_expandable_child_layout.view.*
import kotlinx.android.synthetic.main.etc_expandable_parent_layout.view.*
import kotlinx.android.synthetic.main.list_item_order.view.*

class OrderAdapter (private var orders : MutableList<Order>, private var context : Context) : RecyclerView.Adapter<OrderAdapter.ViewHolder>(){
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(order : Order, context: Context, i : Int){
            itemView.order_detail.parentLayout.setOnClickListener {
                if (itemView.order_detail.isExpanded) { itemView.order_detail.collapse()
                } else { itemView.order_detail.expand() }
            }
            itemView.order_name.text = order.name.toString().toUpperCase().substring(0, 4)
            var totalQuantity = 0
            var totalPrice = 0
            val products = order.products
            products.let {
                totalQuantity = it.size
                totalPrice = it.sumBy {p ->
                    var totalPriceTemp : Int = p.price!!
                    if(p.selectedToppings.isNotEmpty()){
                        var toppingPrice = 0
                        for(t in p.selectedToppings){
                            toppingPrice += t.price!!
                        }
                        totalPriceTemp += toppingPrice
                    }
                    totalPriceTemp
                }
                itemView.order_detail.secondLayout.order_items_rv.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = DetailOrderAdapter(it, context)
                }
            }
            itemView.order_price.text = "Rp. $totalPrice"
            itemView.order_detail.parentLayout.order_item.text = "$totalQuantity items"
            itemView.setOnClickListener {
                Toast.makeText(context, i.toString(), Toast.LENGTH_LONG).show()
                val fragmentManager = context as AppCompatActivity
                ProcessOrderPopup.instance(order, i).show(fragmentManager.supportFragmentManager, "order_popup")
            }
        }
    }

    fun updateList(ord : Order){
        orders.add(ord)
        notifyDataSetChanged()
    }

    fun updateRecords(ords : List<Order>){
        orders.clear()
        orders.addAll(ords)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_order, parent, false))

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(orders[position], context, position)
}