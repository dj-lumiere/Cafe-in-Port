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
import com.ssafy.cafe_in_port_client.common.Constants.MENU_IMGS_URL
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import com.ssafy.cafe_in_port_client.common.util.CommonUtils
import com.ssafy.cafe_in_port_client.data.model.response.OrderDetailResponse
import com.ssafy.cafe_in_port_client.data.model.response.OrderResponse
import com.ssafy.cafe_in_port_client.databinding.ListItemLatestOrderBinding

private const val TAG = "LatestOrderListAdapter"

class LatestOrderListAdapter(
    private var orderList: List<OrderResponse>,
    private val onOrderClick: (OrderResponse) -> Unit
) : RecyclerView.Adapter<LatestOrderListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ListItemLatestOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)
        fun bind(data: OrderResponse) {
            Log.d(TAG, "bindInfo: $data")

            val mainProduct = data.details[0]
            val optionName =
                if (mainProduct.temperature == Temperature.NO && mainProduct.size == DrinkSize.NO) {
                    "기본"
                } else if (mainProduct.temperature == Temperature.NO) {
                    mainProduct.size.toString()
                } else if (mainProduct.size == DrinkSize.NO) {
                    mainProduct.temperature.toString()
                } else {
                    "${mainProduct.size} / ${mainProduct.temperature}"
                }

            val mainProductName = "${mainProduct.productName} (${optionName})"

            // 데이터 바인딩
            if (data.orderCount > 1) {
                textMenuNames.text = itemView.context.getString(
                    R.string.menu_name_multiple, mainProductName, data.orderCount - 1
                )
            } else {
                textMenuNames.text = itemView.context.getString(
                    R.string.menu_name_single, mainProductName
                )
            }
            textMenuPrice.text = itemView.context.getString(
                R.string.menu_price,
                CommonUtils.makeComma(data.totalPrice)
            )
            textMenuDate.text = CommonUtils.dateformatYMDHM(data.orderTime)
            // 이미지 로드

            Glide.with(itemView)
                .load("$MENU_IMGS_URL${data.details[0].productImg}")
                .into(menuImage)
            // 주문하기 버튼 클릭 리스너
            binding.btnGoOrder.setOnClickListener {
                onOrderClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemLatestOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = orderList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    // 데이터 업데이트 메서드
    fun updateData(newOrderList: List<OrderResponse>) {
        orderList = newOrderList
        notifyDataSetChanged()
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
