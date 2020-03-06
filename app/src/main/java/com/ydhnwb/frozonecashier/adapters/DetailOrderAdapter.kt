package com.ydhnwb.frozonecashier.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.frozonecashier.R
import com.ydhnwb.frozonecashier.models.Product
import kotlinx.android.synthetic.main.list_item_detail_order.view.*

class DetailOrderAdapter (private var detailOrders : MutableList<Product>, private var context: Context) : RecyclerView.Adapter<DetailOrderAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context){
            itemView.product_name.text = product.name
            itemView.setOnClickListener {
                Toast.makeText(context, product.name, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_detail_order, parent, false))
    }

    override fun getItemCount() = detailOrders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(detailOrders[position], context)
}