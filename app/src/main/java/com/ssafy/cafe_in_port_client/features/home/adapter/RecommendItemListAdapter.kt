package com.ssafy.cafe_in_port_client.features.home.adapter

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
import com.ssafy.cafe_in_port_client.data.model.dto.Product

private const val TAG = "RecommendItemListAdapte"

class RecommendItemListAdapter(var list: List<Product>) :
    RecyclerView.Adapter<RecommendItemListAdapter.OrderHolder>() {

    private var itemClickListener: ItemClickListener? = null

    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuImage = itemView.findViewById<ImageView>(R.id.recommend_menu_image)
        val textMenuNames = itemView.findViewById<TextView>(R.id.recommend_text_menu_names)

        fun bindInfo(data: Product) {
            Log.d(TAG, "bindInfo: $data")
            Log.d(TAG, "imageURL: ${MENU_IMGS_URL}${data.img}")
            Log.d(TAG, "bindInfo2: $menuImage")
            Glide.with(itemView.context)
                .load("${MENU_IMGS_URL}${data.img}")
                .into(menuImage)
            textMenuNames.text = itemView.context.getString(
                R.string.menu_name_single, data.name
            )
            itemView.setOnClickListener {
                itemClickListener?.onClick(it, bindingAdapterPosition, data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_recommend, parent, false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, productId: Int)
    }
}