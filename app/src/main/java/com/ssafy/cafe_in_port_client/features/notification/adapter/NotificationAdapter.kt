package com.ssafy.cafe_in_port_client.features.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.data.model.dto.NotificationItem

class NotificationAdapter(
    private val notifications: MutableList<NotificationItem>, // 변경 가능한 리스트 사용
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // ViewHolder 정의
    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.noti_icon)
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val message: TextView = itemView.findViewById(R.id.notification_message)
        val deleteButton: ImageView = itemView.findViewById(R.id.notification_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.icon.setImageResource(notification.icon)
        holder.title.text = notification.title
        holder.message.text = notification.message

        // 삭제 버튼 클릭 이벤트
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position) // 추가 작업이 필요하면 호출
        }
    }

    override fun getItemCount(): Int = notifications.size
}