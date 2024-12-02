package com.ssafy.cafe_in_port_client.features.notification.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.cafe_in_port_client.data.model.dto.NotificationItem
import com.ssafy.cafe_in_port_client.features.notification.repository.NotificationRepository

class NotificationViewModel : ViewModel() {
    val notifications: LiveData<MutableList<NotificationItem>> =
        NotificationRepository.notifications

    fun addNotification(notificationItem: NotificationItem) {
        NotificationRepository.addNotification(notificationItem)
    }

    fun getNotification(index: Int): NotificationItem? {
        return NotificationRepository.getNotification(index)
    }

    fun removeNotification(index: Int) {
        NotificationRepository.removeNotification(index)
    }

    fun clearNotifications() {
        NotificationRepository.clearNotifications()
    }
}