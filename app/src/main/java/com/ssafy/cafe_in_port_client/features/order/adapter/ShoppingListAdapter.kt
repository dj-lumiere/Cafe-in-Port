package com.ssafy.cafe_in_port_client.features.order.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.common.Constants.MENU_IMGS_URL
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import com.ssafy.cafe_in_port_client.common.util.CommonUtils
import com.ssafy.cafe_in_port_client.data.model.dto.ShoppingCart
import com.ssafy.cafe_in_port_client.databinding.ListItemShoppingListBinding

private const val TAG = "ShoppingListAdapter_싸피"

class ShoppingListAdapter(
    var list: MutableList<ShoppingCart>,
    private val onDeleteClick: (Int) -> Unit,
    private val onPlusClick: (Int) -> Unit,
    private val onMinusClick: (Int) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>() {

    inner class ShoppingListHolder(private val binding: ListItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(position: Int) {
            val item = list[position]

            Glide.with(itemView).load("${MENU_IMGS_URL}${item.menuImg}")
                .into(binding.itemImage)
            binding.itemName.text = item.menuName
            binding.itemPriceValue.text = CommonUtils.makeComma(item.menuPrice)
            binding.itemQuantityText.text = "${item.menuCnt}"
            binding.itemTotalPrice.text = CommonUtils.makeComma(item.menuPrice * item.menuCnt)
            binding.itemSizeValue.text =
                if (item.temperature == Temperature.NO && item.size == DrinkSize.NO) {
                    "기본"
                } else if (item.temperature == Temperature.NO) {
                    item.size.toString()
                } else if (item.size == DrinkSize.NO) {
                    item.temperature.toString()
                } else {
                    "${item.size} / ${item.temperature}"
                }
            Log.e(TAG, "bindInfo:$position")

            if (item.menuCnt == 1) {
                binding.itemMinusButton.background = null
                binding.itemMinusButton.setBackgroundResource(R.drawable.ic_delete_24)
            } else {
                binding.itemMinusButton.background = null
                binding.itemMinusButton.setBackgroundResource(R.drawable.ic_remove_24)
            }

            binding.itemDeleteButton.setOnClickListener {
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(currentPosition)
                }
            }

            binding.itemPlusButton.setOnClickListener {
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onPlusClick(currentPosition)
                }
            }

            binding.itemMinusButton.setOnClickListener {
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onMinusClick(currentPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        return ShoppingListHolder(
            ListItemShoppingListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}