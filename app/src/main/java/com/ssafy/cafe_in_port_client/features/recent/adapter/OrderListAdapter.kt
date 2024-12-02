package com.ssafy.cafe_in_port_client.features.recent.adapter

import android.util.Log
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
import com.ssafy.cafe_in_port_client.data.model.response.OrderResponse
import com.ssafy.cafe_in_port_client.common.enums.OrderStatus
import com.ssafy.cafe_in_port_client.common.util.CommonUtils

private const val TAG = "OrderListAdapter"

class OrderListAdapter(var list: List<OrderResponse>) :
    RecyclerView.Adapter<OrderListAdapter.OrderHolder>() {

    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)
        val textCompleted = itemView.findViewById<TextView>(R.id.textCompleted)

        fun bindInfo(data: OrderResponse) {
            Log.d(TAG, "bindInfo: $data")

            Glide.with(itemView)
                .load("${MENU_IMGS_URL}${data.details[0].productImg}")
                .into(menuImage)

            if (data.orderCount > 1) {
                textMenuNames.text = itemView.context.getString(
                    R.string.menu_name_multiple, data.details[0].productName, data.orderCount - 1
                )
            } else {
                textMenuNames.text = itemView.context.getString(
                    R.string.menu_name_single, data.details[0].productName
                )
            }

            textMenuPrice.text = CommonUtils.makeComma(data.totalPrice)
            textMenuDate.text = CommonUtils.dateformatYMDHM(data.orderTime)
            textCompleted.text = when (CommonUtils.isOrderCompleted(data)) {
                OrderStatus.PENDING -> "주문 확인 중"
                OrderStatus.PROCESSING -> "음식을 만들고 있어요"
                OrderStatus.COMPLETED -> "주문이 완료되었어요"
                OrderStatus.CANCELLED -> "주문이 취소되었어요"
                OrderStatus.RETURNED -> "환불이 신청되었어요"
                OrderStatus.REFUNDED -> "환불이 완료되었어요"
            }
            //클릭연결
            itemView.setOnClickListener {
                Log.d(TAG, "bindInfo: ${data.orderId}")
                itemClickListner.onClick(it, layoutPosition, data.orderId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_order, parent, false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View, position: Int, orderid: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}