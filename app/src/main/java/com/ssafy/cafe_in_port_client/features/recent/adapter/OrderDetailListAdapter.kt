package com.ssafy.cafe_in_port_client.features.recent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.common.Constants.MENU_IMGS_URL
import com.ssafy.cafe_in_port_client.data.model.response.OrderDetailResponse
import com.ssafy.cafe_in_port_client.common.util.CommonUtils

class OrderDetailListAdapter(val context: Context, var orderResponseList: List<OrderDetailResponse>) :
    RecyclerView.Adapter<OrderDetailListAdapter.OrderDetailListHolder>(){

    inner class OrderDetailListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textShoppingMenuName = itemView.findViewById<TextView>(R.id.textShoppingMenuName)
        val textShoppingMenuMoney = itemView.findViewById<TextView>(R.id.textShoppingMenuMoney)
        val textShoppingMenuCount = itemView.findViewById<TextView>(R.id.textShoppingMenuCount)
        val textShoppingMenuMoneyAll = itemView.findViewById<TextView>(R.id.textShoppingMenuMoneyAll)

        fun bindInfo(data: OrderDetailResponse){

            Glide.with(itemView)
                .load("${MENU_IMGS_URL}${data.productImg}")
                .into(menuImage)

            textShoppingMenuName.text = data.productName
            textShoppingMenuMoney.text = CommonUtils.makeComma(data.unitPrice)
            textShoppingMenuCount.text = "${data.quantity}"
            textShoppingMenuMoneyAll.text = CommonUtils.makeComma(data.unitPrice * data.quantity)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_order_detail_list, parent, false)
        return OrderDetailListHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailListHolder, position: Int) {
        holder.bindInfo(orderResponseList[position])
    }

    override fun getItemCount(): Int {
        return orderResponseList.size
    }
}